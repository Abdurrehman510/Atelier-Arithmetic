# sign.ps1 - Automated Windows EV Code Signing Script for Atelier Arithmetic
# Uses signtool.exe to sign executables and installers with SHA-256 and timestamping

param(
    [string]$TargetFile = "dist\AtelierArithmetic\AtelierArithmetic.exe",
    [string]$CertPath = "",
    [string]$CertPassword = "",
    [string]$CryptoProvider = "eToken.dll",
    [string]$ContainerName = "",
    [string]$Pin = ""
)

# 1. Discover signtool.exe dynamically from Windows SDK directories
$sdkPaths = @(
    "C:\Program Files (x86)\Windows Kits\10\bin\*\x64\signtool.exe",
    "C:\Program Files\Windows Kits\10\bin\*\x64\signtool.exe",
    "C:\Program Files (x86)\Windows Kits\8.1\bin\x64\signtool.exe"
)

$signtool = $null
foreach ($path in $sdkPaths) {
    $found = Resolve-Path $path -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Path -First 1
    if ($found) {
        $signtool = $found
        break
    }
}

if (-not $signtool) {
    Write-Warning "signtool.exe could not be found automatically. Please ensure Windows SDK is installed."
    Write-Host "Trying to fall back to PATH search..."
    $signtool = Get-Command signtool.exe -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source
}

if (-not $signtool) {
    Write-Error "signtool.exe was not found. Please install the Windows SDK or add signtool.exe to your PATH."
    exit 1
}

Write-Host "Using SignTool: $signtool"

# 2. Check if target file exists
if (-not (Test-Path $TargetFile)) {
    Write-Error "Target file not found: $TargetFile"
    exit 1
}

# 3. Construct signtool command
# Using DigiCert's RFC 3161 timestamp server to verify signing time after certificate expires
$timestampUrl = "http://timestamp.digicert.com"

# Prepare command line arguments
$signArgs = @("sign", "/fd", "SHA256", "/tr", $timestampUrl, "/td", "SHA256")

if (-not [string]::IsNullOrEmpty($CertPath)) {
    # Method A: Local PFX / Software Certificate (ideal for developer staging and testing)
    Write-Host "Signing using local software certificate PFX: $CertPath"
    $signArgs += @("/f", $CertPath)
    if (-not [string]::IsNullOrEmpty($CertPassword)) {
        $signArgs += @("/p", $CertPassword)
    }
} elseif (-not [string]::IsNullOrEmpty($Pin)) {
    # Method B: Extended Validation (EV) Hardware Token / Smart Card (Production Pipeline)
    Write-Host "Signing using EV Hardware Token..."
    $signArgs += @("/csp", "Microsoft Base Smart Card Crypto Provider")
    $signArgs += @("/k", "[$Pin]=$ContainerName")
} else {
    # Method C: Interactive lookup in User/Machine Certificate Store
    Write-Host "Signing using certificate from Windows Certificate Store (auto-select)..."
    $signArgs += @("/a")
}

$signArgs += $TargetFile

# 4. Execute SignTool
Write-Host "Executing: & '$signtool' $signArgs"
& $signtool $signArgs

# 5. Verify the signature
if ($LASTEXITCODE -eq 0) {
    Write-Host "Signing completed successfully. Verifying application integrity..."
    & $signtool verify /pa $TargetFile
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Integrity verification PASSED!"
    } else {
        Write-Error "Signature verification FAILED!"
        exit 1
    }
} else {
    Write-Error "Signing process failed with exit code: $LASTEXITCODE"
    exit 1
}
