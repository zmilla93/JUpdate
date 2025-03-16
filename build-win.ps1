$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
$mavenOutput = $true

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
    throw("Failed to build JAR!")
}

Write-Host "Running JDEPS... " -NoNewline
$jdepsTime = Measure-Command { $JDEPS = jdeps --print-module-deps --ignore-missing-deps target/jar/$APP_NAME.jar }
Write-Host "$( "{0:N3}" -f $jdepsTime.TotalSeconds )s"
Write-Host "Dependencies: $JDEPS"

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

Write-Host "Building Windows Installer... " -NoNewLine
$msiTime = Measure-Command {
    jpackage --type msi `
    --name "$APP_NAME" `
    --main-jar "$APP_NAME.jar" `
    --main-class $MAIN_CLASS `
    --app-version $APP_VERSION `
    --runtime-image target/jre `
    --input target/jar `
    --dest target/win-msi `
    --arguments '--distribution:win-msi' `
    --win-per-user-install `
    --win-console `
    --win-shortcut-prompt `
    --win-shortcut `
    --win-menu
}
Write-Host "$( "{0:N2}" -f $msiTime.TotalSeconds )s"

Write-Host $sep
Write-Host "Build Process Complete"
Write-Host $sep
Write-Host ".jar     : $( "{0:N2}" -f $jarTime.TotalSeconds )s"
Write-Host "jdeps    : $( "{0:N2}" -f $jdepsTime.TotalSeconds )s"
Write-Host "jlink    : $( "{0:N2}" -f $jlinkTime.TotalSeconds )s"
Write-Host "portable : $( "{0:N2}" -f $portableTime.TotalSeconds )s"
Write-Host "msi      : $( "{0:N2}" -f $msiTime.TotalSeconds )s"
Write-Host $sep
Write-Host "Total  : $( "{0:N2}" -f $stopwatch.Elapsed.TotalSeconds )s"
Write-Host $sep
