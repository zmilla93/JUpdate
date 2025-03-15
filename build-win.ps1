
Write-Host $path
Write-Host "Reading pom.xml..."
[xml]$pom = Get-Content pom.xml
$APP_NAME = $pom.project.artifactId
$APP_VERSION = $pom.project.version
$MAIN_CLASS = $pom.project.properties."main-class"
$JAVA_VERSION = $pom.project.properties."java-version"
Write-Host "App: $APP_NAME"
Write-Host "Version: $APP_VERSION"
Write-Host "Java: $JAVA_VERSION"
Write-Host "Main: $MAIN_CLASS"

Write-Host "Building JAR..."
mvn clean compile assembly:single

Write-Host "Running jeps..."
$JDEPS = jdeps --print-module-deps --ignore-missing-deps target/jar/$APP_NAME.jar
Write-Host "JDEPS: $JDEPS"

Write-Host "Building JRE..."
jlink `
    --output target/jre `
    --strip-native-commands `
    --strip-debug `
    --no-man-pages `
    --no-header-files `
    --add-modules $JDEPS

Write-Host "Building Windows Portable..."
jpackage --type app-image `
    --name JUpdater `
    --main-jar JUpdate.jar `
    --main-class $MAIN_CLASS `
    --runtime-image target/jre `
    --input target/jar `
    --dest target/win-portable `
    --win-console

Write-Host "Building Windows Installer (MSI)..."
jpackage --type msi `
    --name "$APP_NAME" `
    --main-jar "$APP_NAME.jar" `
    --main-class $MAIN_CLASS `
    --app-version $APP_VERSION `
    --runtime-image target/jre `
    --input target/jar `
    --dest target/win-msi `
    --win-per-user-install `
    --win-console `
    --win-shortcut-prompt `
    --win-shortcut `
    --win-menu

Write-Host "Build process completed successfully!"