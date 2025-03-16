# Patches an app that uses MSI distribution
$launcher = $null
$installer = $null
$launcherPrefix = "--launcher:"
$installerPrefix = "--installer:"
# Get the launcher and installer paths from the program args
#   --launcher:"path/to/launcher"
#   --installer:"path/to/installer"
Write-Host "Arg Count: $( $args.Count )"
foreach ($arg in $args)
{
    Write-Host "Current Arg: $arg"
    if ( $arg.StartsWith($launcherPrefix))
    {
        Write-Host "Launcher Arg: $arg"
        $launcher = $arg.Replace($launcherPrefix, "")
    }
    if ( $arg.StartsWith($installerPrefix))
    {
        Write-Host "Installer Arg: $arg"
        $installer = $arg.Replace($installerPrefix, "")
    }
}
if ($null -eq $launcher)
{
    throw("No launcher argument specified!")
}
if ($null -eq $installer)
{
    throw("No installer argument specified!")
}
Write-Host "Launcher  : $launcher"
Write-Host "Installer : $installer"
# Run the new installer while displaying a progress bar
Start-Process -FilePath "$installer" -ArgumentList "/passive" -Wait
# Launch the newly installed program
$newArgs = $args + "clean"
Write-Host $newArgs
Start-Process $launcher -ArgumentList $newArgs