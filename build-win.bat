@ECHO off
echo Building JAR...
call mvn clean compile assembly:single

echo Building JRE...
call jlink ^
    --module-path "%JAVA_HOME%/jmods" ^
    --output target/jre ^
    --strip-native-commands ^
    --strip-debug ^
    --no-man-pages ^
    --no-header-files ^
    --add-modules java.base,java.compiler,java.desktop,java.sql

echo Building Windows Portable...
call jpackage --type app-image ^
    --name JUpdater ^
    --main-jar JUpdater.jar ^
    --main-class io.github.zmilla93.MainKt ^
    --runtime-image target/jre ^
    --input target/jar ^
    --dest target/win-portable ^
    --win-console

echo Building Windows Installer (MSI)...
call jpackage --type msi ^
    --name JUpdater ^
    --main-jar JUpdater.jar ^
    --main-class io.github.zmilla93.MainKt ^
    --app-version 1.0.0 ^
    --runtime-image target/jre ^
    --input target/jar ^
    --dest target/win-msi ^
    --win-per-user-install ^
    --win-console ^
    --win-shortcut-prompt ^
    --win-shortcut ^
    --win-menu
