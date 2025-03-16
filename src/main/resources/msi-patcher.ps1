####################################################
# Patches for Windows Installer (MSI) distribution #
####################################################

# Global Variables
$launcher = $null
$installer = $null
$launcherPrefix = "--launcher:"
$installerPrefix = "--installer:"
# FIXME : Make this dynamic. Use replaceFirst when writing the local patcher file using config.appName.
$appName = "JUpdater"
# Get the launcher and installer paths from the program args
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
# Append "--clean" to the arguments list that
$newArgs = $args + "--clean"
# Quit early if launcher param isn't set correctly.
# This is really bad, since it means the main program can't relaunch.
if ($null -eq $launcher)
{
    # Show the user a dialog box to tell them some someone messed up :^)
    Add-Type -AssemblyName PresentationFramework
    [System.Windows.MessageBox]::Show("The MSI patcher wasn't given a launcher path and cannot restart. Make sure your installer is up to date, or report a bug if the problem persists.", "$appName Crashed", "OK", "Error")
    throw("No launcher argument specified!")
}
if ($null -eq $installer)
{
    Add-Type -AssemblyName PresentationFramework
    [System.Windows.MessageBox]::Show("The MSI patcher wasn't given an installer path. Relaunching $appName without updating.", "Update Failed", "Ok", "Warning")
    Start-Process $launcher
    throw("No installer argument specified!")
}
Write-Host "Launcher  : $launcher"
Write-Host "Installer : $installer"
# Run the new installer while displaying a progress bar
Start-Process -FilePath "$installer" -ArgumentList "/passive" -Wait
# Launch the newly installed program

Write-Host $newArgs
Start-Process $launcher -ArgumentList $newArgs