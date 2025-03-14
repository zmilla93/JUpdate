package io.github.zmilla93

import io.github.zmilla93.gui.MainFrame
import io.github.zmilla93.jupdate.JarUpdater
import io.github.zmilla93.jupdate.UpdaterConfig
import io.github.zmilla93.updater.ProjectProperties
import updater.data.AppVersion
import java.nio.file.Paths
import javax.swing.SwingUtilities

//val updateManager: UpdateManager


fun main(args: Array<String>) {

    println("App Launched: ${args.joinToString(separator = ",")}")
    val properties = ProjectProperties()
    val version = AppVersion(properties.version)
//    handleUpdateProcess(args, version)
    SwingUtilities.invokeLater {
        val mainFrame = MainFrame(args, version)
        mainFrame.isVisible = true
    }
//    val path = File(".").canonicalPath
//    println("LaunchTest1: $path")
//    println("LaunchTest2: ${System.getProperty("user.dir")}")

}

fun handleUpdateProcess(args: Array<String>, currentVersion: AppVersion) {
    val jarName = "JUpdater.jar"
    val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp")
    val jarUpdaterConfig = UpdaterConfig(arrayOf(jarName), jarName, tempDir)
    val updater = JarUpdater("zmilla93", "JUpdate", currentVersion, jarUpdaterConfig)
    updater.handleCurrentlyRunningUpdate(args)
    if (updater.wasJustUpdated()) return
    if (updater.isUpdateAvailable()) updater.startUpdateProcess()
}