# package.ps1 - Automated Build, Icon Translation, and Windows Native Packaging Pipeline
# Resolves zero-dependency packaging, custom runtime generation, and MSI installers.

# Enable ErrorActionPreference to halt on failure
$ErrorActionPreference = "Stop"

Write-Host "=========================================================="
Write-Host "ATELIER ARITHMETIC - WINDOWS PACKAGING & DISTRIBUTION PIPELINE"
Write-Host "=========================================================="

# 1. Environment Verification
Write-Host "[1/5] Verifying environment and build tools..."

if (-not $env:JAVA_HOME) {
    Write-Warning "JAVA_HOME environment variable is not defined. Attempting to run java commands from PATH..."
} else {
    Write-Host "Found JAVA_HOME: $env:JAVA_HOME"
    # Ensure JDK binaries are in PATH if possible
    $env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
}

# Verify java compilation tools are available
try {
    & javac --version | Out-Null
    & jar --version | Out-Null
    & jpackage --version | Out-Null
} catch {
    Write-Error "Required JDK tools (javac, jar, jpackage) were not found on the path. Please ensure JDK 17+ is installed."
    exit 1
}

# Verify WiX Toolset for MSI generation
$hasWix = $false
try {
    $candle = Get-Command candle.exe -ErrorAction SilentlyContinue
    $light = Get-Command light.exe -ErrorAction SilentlyContinue
    if ($candle -and $light) {
        $hasWix = $true
        Write-Host "Found WiX Toolset (MSI compiler ready)."
    } else {
        Write-Warning "WiX Toolset (candle.exe/light.exe) was not found on PATH. Installer generation (.msi) will be skipped, falling back to portable ZIP."
    }
} catch {
    Write-Warning "Failed checking for WiX Toolset. Falling back to portable ZIP."
}

# 2. Cleanup old build files
Write-Host "[2/5] Cleaning up old build outputs..."
if (Test-Path "build") { Remove-Item -Recurse -Force "build" }
if (Test-Path "dist") { Remove-Item -Recurse -Force "dist" }

# 3. Icon Translation & Code Compilation
Write-Host "[3/5] Translating assets and compiling application..."

# Compile source files including custom IcoConverter utility
mkdir -Force "build/classes"
& javac -cp "lib/*" -sourcepath src src\com\mathquiz\QuizApp.java src\com\mathquiz\util\IcoConverter.java -d build/classes

# Execute IcoConverter to construct Microsoft Icon format logo
Write-Host "Creating high-res logo.ico from logo.png..."
& java -cp build/classes com.mathquiz.util.IcoConverter src/com/mathquiz/resources/logo.png logo.ico

# Copy resources so they are bundled in the JAR
mkdir -Force "build/classes/com/mathquiz/resources"
Copy-Item -Path src/com/mathquiz/resources/logo.png -Destination build/classes/com/mathquiz/resources/logo.png

# Bundle main application executable JAR
mkdir -Force "dist"
& jar --create --file dist/atelier-arithmetic.jar --main-class com.mathquiz.QuizApp -C build/classes .

# Set up input directory for jpackage
mkdir -Force "build/input"
Copy-Item -Path "dist/atelier-arithmetic.jar" -Destination "build/input/"
Copy-Item -Path "lib/sqlite-jdbc-3.42.0.0.jar" -Destination "build/input/"

# 4. Native App Image & Custom Minimal Runtime Generation (jlink)
Write-Host "[4/5] Running jpackage to generate native app image..."

$jpackageCommonArgs = @(
    "--name", "AtelierArithmetic",
    "--input", "build/input",
    "--main-jar", "atelier-arithmetic.jar",
    "--main-class", "com.mathquiz.QuizApp",
    "--add-modules", "java.base,java.desktop,java.sql,java.xml,java.naming,jdk.charsets",
    "--jlink-options", "--strip-debug --no-man-pages --no-header-files --compress zip-9",
    "--icon", "logo.ico",
    "--app-version", "1.0.0",
    "--vendor", "Atelier Arithmetic",
    "--description", "Intelligent child-friendly math learning game with adaptive difficulty, gamified shop economy, and premium analytics.",
    "--dest", "dist"
)

# Run jpackage to create portable app image folder
& jpackage --type app-image $jpackageCommonArgs

Write-Host "Native app-image successfully created at: dist\AtelierArithmetic"

# 5. Installer Generation or Portable ZIP Packaging
Write-Host "[5/5] Packaging release bundle..."

if ($hasWix) {
    Write-Host "Compiling native MSI Windows Installer using jpackage..."
    $installerArgs = $jpackageCommonArgs + @(
        "--type", "msi",
        "--win-menu",
        "--win-shortcut",
        "--win-dir-chooser",
        "--win-upgrade-uuid", "71a2e7c3-3882-4df4-94fa-7e9b422a59cc",
        "--win-menu-group", "Atelier Arithmetic"
    )
    & jpackage $installerArgs
    Write-Host "Native Windows MSI Installer successfully created at: dist\AtelierArithmetic-1.0.0.msi"
} else {
    Write-Host "Compressing native app-image folder into portable ZIP archive..."
    $zipPath = "dist\AtelierArithmetic-v1.0.0-Portable.zip"
    # Compress app-image folder to zip
    Compress-Archive -Path "dist\AtelierArithmetic" -DestinationPath $zipPath -Force
    Write-Host "Portable ZIP bundle successfully created at: $zipPath"
}

Write-Host "=========================================================="
Write-Host "BUILD & PACKAGING PIPELINE COMPLETE!"
Write-Host "=========================================================="
