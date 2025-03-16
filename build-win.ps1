$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
$mavenOutput = $true

$failed = $false
$failedJDeps = $false
$failedJLink = $false
$failedWinMSI = $false
$failedWinPortable = $false
# FIXME : Only maven build errors are reported, need to check status on all build steps.
$sep = "--------------------------------------------------"
Write-Host $sep
Write-Host "Running Windows Build Process"
Write-Host $sep

Write-Host "Reading pom.xml... " -NoNewLine
$pomTime = Measure-Command {
    [xml]$pom = Get-Content pom.xml
    $APP_NAME = $pom.project.artifactId
    $APP_VERSION = $pom.project.version
    $MAIN_CLASS = $pom.project.properties."main-class"
    $test = $pom.nope
    $JAVA_VERSION = $pom.project.properties."java-version"
}
Write-Host "$( "{0:N3}" -f $pomTime.TotalSeconds )s"
Write-Host "`tJava : $JAVA_VERSION"
Write-Host "`tApp  : $APP_NAME v$APP_VERSION"
Write-Host "`tMain : $MAIN_CLASS"

if ($mavenOutput)
{
    Write-Host "Building JAR... "
    $jarTime = Measure-Command { mvn clean compile assembly:single | Out-Default }
}
else
{
    Write-Host "Building JAR... " -NoNewLine
    $jarTime = Measure-Command { mvn clean compile assembly:single }
    Write-Host "$( "{0:N3}" -f $jarTime.TotalSeconds )s"
}
if ($LASTEXITCODE -ne 0)
{
    mvn clean
    if($LASTEXITCODE -eq 0){
        throw("Failed to build JAR, but project was successfully cleaned. Make sure the project builds locally before building for distribution.")
    }else{
        throw("Failed to clean project. Make sure no other programs are using the 'target' folder, then try again.")
    }
}

Write-Host "Running JDEPS... " -NoNewline
$jdepsTime = Measure-Command { $JDEPS = jdeps --print-module-deps --ignore-missing-deps target/jar/$APP_NAME.jar }
Write-Host "$( "{0:N3}" -f $jdepsTime.TotalSeconds )s"
Write-Host "Dependencies: $JDEPS"
if ($LASTEXITCODE -ne 0)
{
    throw("Failed to find dependencies using JDEPS!")
}

Write-Host "Building JRE... " -NoNewLine
$jlinkTime = Measure-Command {
    jlink `
    --output target/jre `
    --strip-native-commands `
    --strip-debug `
    --no-man-pages `
    --no-header-files `
    --add-modules $JDEPS
}
Write-Host "$( "{0:N2}" -f $jlinkTime.TotalSeconds )s"
if ($LASTEXITCODE -ne 0)
{
    throw("Failed to build JRE using jlink!")
}

Write-Host "Building Windows Portable... " -NoNewline
$portableTime = Measure-Command {
    jpackage --type app-image `
    --name JUpdater `
    --main-jar JUpdate.jar `
    --main-class $MAIN_CLASS `
    --runtime-image target/jre `
    --input target/jar `
    --dest target/win-portable `
    --arguments '--distribution:win-portable' `
    --win-console
}
Write-Host "$( "{0:N2}" -f $portableTime.TotalSeconds )s"
if ($LASTEXITCODE -ne 0)
{
    Write-Host "Failed to build Windows Portable!"
    $failed = $true
    $failedWinPortable = $true
}

Write-Host "Building Windows Installer... " -NoNewLine
# FIXME : Vendor name
$msiTime = Measure-Command {
    jpackage --type msi `
    --name "$APP_NAME" `
    --vendor zmilla93 `
    --main-jar "$APP_NAME.jar" `
    --main-class $MAIN_CLASS `
    --app-version $APP_VERSION `
    --runtime-image target/jre `
    --input target/jar `
    --dest target/win-msi `
    --resource-dir 'jpackage' `
    --arguments '--distribution:win-msi' `
    --win-per-user-install `
    --win-console `
    --win-shortcut-prompt `
    --win-shortcut `
    --win-menu
}
Write-Host "$( "{0:N2}" -f $msiTime.TotalSeconds )s"
if ($LASTEXITCODE -ne 0)
{
    Write-Host "Failed to build Windows Installer!"
    $failed = $true
    $failedWinMSI = $true
}

$title
$successPrefix = "> "
$failPrefix =    "[FAILURE] >>>>>>>>>>>>>>>>>>>> "
if($failed){
    $title = "BUILD FAILED >:("
}else {
    $title = "Build Success :)"
}

Write-Host $sep
Write-Host $title
Write-Host $sep
# Hard coded success for steps what will intentionally crash early when failed.
Write-Host $successPrefix -NoNewLine
Write-Host ".jar     : $( "{0:N2}" -f $jarTime.TotalSeconds )s"
Write-Host $successPrefix -NoNewLine
Write-Host "jdeps    : $( "{0:N2}" -f $jdepsTime.TotalSeconds )s"
Write-Host $successPrefix -NoNewLine
Write-Host "jre      : $( "{0:N2}" -f $jlinkTime.TotalSeconds )s"
if($failedWinPortable) {
    Write-Host $failPrefix -NoNewLine
}else{
    Write-Host $successPrefix -NoNewLine
}
Write-Host "portable : $( "{0:N2}" -f $portableTime.TotalSeconds )s"
if($failedWinMSI) {
    Write-Host $failPrefix -NoNewLine
}else{
    Write-Host $successPrefix -NoNewLine
}
Write-Host "msi      : $( "{0:N2}" -f $msiTime.TotalSeconds )s"
Write-Host $sep
Write-Host "Total  : $( "{0:N2}" -f $stopwatch.Elapsed.TotalSeconds )s"
Write-Host $sep
