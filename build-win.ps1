$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()

Write-Host $path
Write-Host "Reading pom.xml..." -NoNewline
$time = Measure-Command {
[xml]$pom = Get-Content pom.xml
$APP_NAME = $pom.project.artifactId
$APP_VERSION = $pom.project.version
$MAIN_CLASS = $pom.project.properties."main-class"
$JAVA_VERSION = $pom.project.properties."java-version"
Write-Host "Java: $JAVA_VERSION"
Write-Host "App: $APP_NAME v$APP_VERSION"
Write-Host "Main: $MAIN_CLASS"
}
Write-Host "$("{0:N3}" -f $time.TotalSeconds)s"

Write-Host "Building JAR... "
mvn clean compile assembly:single

Write-Host "Running JDEPS... " -NoNewline
$time = Measure-Command {
$JDEPS = jdeps --print-module-deps --ignore-missing-deps target/jar/$APP_NAME.jar
}
Write-Host "$("{0:N3}" -f $time.TotalSeconds)s"
Write-Host "Dependencies: $JDEPS"

Write-Host "Building JRE..."
$time = Measure-Command {
jlink `
    --output target/jre `
    --strip-native-commands `
    --strip-debug `
    --no-man-pages `
    --no-header-files `
    --add-modules $JDEPS
}
Write-Host "$("{0:N3}" -f $time.TotalSeconds)s"

Write-Host "Building Windows Portable... " -NoNewline
$time = Measure-Command {
jpackage --type app-image `
    --name JUpdater `
    --main-jar JUpdate.jar `
    --main-class $MAIN_CLASS `
    --runtime-image target/jre `
    --input target/jar `
    --dest target/win-portable `
    --arguments '--distribution-type:win-portable' `
    --win-console
}
Write-Host "$("{0:N3}" -f $time.TotalSeconds)s"

Write-Host "Building Windows Installer... "
$time = Measure-Command {
jpackage --type msi `
    --name "$APP_NAME" `
    --main-jar "$APP_NAME.jar" `
    --main-class $MAIN_CLASS `
    --app-version $APP_VERSION `
    --runtime-image target/jre `
    --input target/jar `
    --dest target/win-msi `
    --arguments '--distribution-type:win-msi' `
    --win-per-user-install `
    --win-console `
    --win-shortcut-prompt `
    --win-shortcut `
    --win-menu
}
Write-Host "$("{0:N3}" -f $time.TotalSeconds)s"

Write-Host "======================"
Write-Host "Build Process Complete"
Write-Host "Total Time: $($stopwatch.Elapsed.TotalSeconds)s"
Write-Host "======================"
