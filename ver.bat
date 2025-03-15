@REM [xml]$pom = Get-Content -Path "pom.xml"
@REM $pom.project.version
powershell -NoProfile -ExecutionPolicy Bypass -Command "& {
    [xml]$pom = Get-Content 'pom.xml';
    $version = $pom.project.version;
    if (-not $version) { $version = $pom.project.parent.version };
    if ($version -match '\$\{(.+?)\}') {
        $propertyName = $matches[1];
        $version = $pom.project.properties.$propertyName;
    };
    Write-Output $version
}"