package io.github.zmilla93

import io.github.zmilla93.gui.MainFrame
import io.github.zmilla93.jupdate.GitHubUpdaterConfig
import io.github.zmilla93.jupdate.JarUpdater
import io.github.zmilla93.jupdate.UpdateUtil
import io.github.zmilla93.updater.data.DistributionType
import io.github.zmilla93.updater.data.ProjectProperties
import updater.data.AppVersion
import java.nio.file.Paths
import javax.swing.SwingUtilities

//val updateManager: UpdateManager


fun main(args: Array<String>) {

    println("App Launched: ${args.joinToString(prefix = "[", postfix = "]", separator = ",")}")
    val properties = ProjectProperties()
    val version = AppVersion(properties.version)
//    val distributionType = DistributionType.getTypeFromArgs(UpdateUtil.getCurrentProgramPath())
    println("Version: " + properties.version)
//    println("Distribution:" + distributionType)

    handleUpdateProcess(args, version)
    SwingUtilities.invokeLater {
        val mainFrame = MainFrame(args, version)
        mainFrame.isVisible = true
    }
//    val path = File(".").canonicalPath
//    println("LaunchTest1: $path")
//    println("LaunchTest2: ${System.getProperty("user.dir")}")

}

fun handleUpdateProcess(args: Array<String>, currentVersion: AppVersion) {
    // FIXME @important : Use app name from pom
    val jarName = "JUpdate.jar"
    val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp")
    val jarUpdaterConfig = GitHubUpdaterConfig(arrayOf(jarName), jarName, tempDir)
    System.err.println("updater disabled")
    val updater = JarUpdater("zmilla93", "JUpdate", currentVersion, jarUpdaterConfig)
//    createUpdater()
    updater.handleCurrentlyRunningUpdate(args)
    println("Distribution Type: " + DistributionType.getTypeFromArgs(args))
    if (updater.wasJustUpdated()) {
        println("Was just updated!")
        return
    }
//    if (updater.isUpdateAvailable()) updater.startUpdateProcess()
}

fun createUpdater() {
//    var updater:AbstractGitHubUpdater;
//    val distributionType = DistributionType.current(UpdateUtil.getCurrentProgramPath())
//    println("Distribution:" + distributionType)

}