# test.ps1 - Compile the app and its tests, then run JUnit 5 with a throw-away user.home.
# Works in Windows PowerShell 5.1 and PowerShell 7 (Windows/macOS/Linux).
# Tests never touch the real ~/.atelier-arithmetic folder (rules.md R-TST-07).

$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

$junitJar = 'lib-test/junit-platform-console-standalone-1.11.4.jar'
$sqliteJar = 'lib/sqlite-jdbc-3.42.0.0.jar'
$sep = [IO.Path]::PathSeparator

if (Test-Path 'build/test') { Remove-Item -Recurse -Force 'build/test' }
New-Item -ItemType Directory -Force 'build/test/main', 'build/test/classes', 'build/test/home' | Out-Null

function Write-SourceList([string]$root, [string]$listFile) {
    $files = Get-ChildItem -Path $root -Recurse -Filter '*.java' |
        ForEach-Object { '"' + ($_.FullName -replace '\\', '/') + '"' }
    Set-Content -Path $listFile -Value $files -Encoding ascii
}

# 1. Main sources (all of them, so test-only consumers of non-UI classes compile too)
Write-SourceList 'src' 'build/test/main-sources.txt'
& javac -encoding UTF-8 --release 17 -cp 'lib/*' -d 'build/test/main' '@build/test/main-sources.txt'
if ($LASTEXITCODE -ne 0) { Write-Host 'Main sources failed to compile.'; exit $LASTEXITCODE }
# Resources (the logo) too, as run.bat and package.ps1 do, so layout tests see the screens users see
$resOut = New-Item -ItemType Directory -Force 'build/test/main/com/mathquiz/resources'
Copy-Item -Path 'src/com/mathquiz/resources/*' -Destination $resOut -Recurse -Force

# 2. Test sources
Write-SourceList 'test' 'build/test/test-sources.txt'
$testCp = "build/test/main$sep" + "lib/*$sep" + 'lib-test/*'
& javac -encoding UTF-8 --release 17 -cp $testCp -d 'build/test/classes' '@build/test/test-sources.txt'
if ($LASTEXITCODE -ne 0) { Write-Host 'Test sources failed to compile.'; exit $LASTEXITCODE }

# 3. Run every test with a throw-away home folder
# Forward slashes: Oracle's javapath java.exe shim doubles backslashes inside -D values.
$testHome = (Resolve-Path 'build/test/home').Path -replace '\\', '/'
$runCp = "build/test/classes$sep" + "build/test/main$sep" + $sqliteJar
& java "-Duser.home=$testHome" -jar $junitJar execute `
    --class-path $runCp `
    --scan-class-path build/test/classes `
    --fail-if-no-tests `
    --disable-banner `
    --details=tree
exit $LASTEXITCODE
