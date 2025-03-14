package io.github.zmilla93

import io.github.zmilla93.gui.MainFrame
import io.github.zmilla93.jupdate.JarUpdater
import io.github.zmilla93.jupdate.UpdaterConfig
import io.github.zmilla93.updater.ProjectProperties
import updater.data.AppInfo
import updater.data.AppVersion
import java.io.File
import java.nio.file.Paths
import javax.swing.SwingUtilities

//val updateManager: UpdateManager


fun main(args: Array<String>) {
    println("Hello World!")
    SwingUtilities.invokeLater {
        val mainFrame = MainFrame(args)
        mainFrame.isVisible = true
    }
    handleUpdateProcess(args)
//    println("VERSION : " + properties.version)
//    val updateManger = UpdateManager(
//        "zmilla93",
//        "SlimTrade",
//        // FIXME : Temp path
//        Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp"),
//        appInfo,
//        false
//    )


//    println("Update available: ${updater.isUpdateAvailable()}")
//    if (updater.isUpdateAvailable())
//    updater.download()
    val path = File(".").canonicalPath
//    println("Launch1: ${updater.getLaunchPath()}")
    println("Launch2: $path")
    println("Launch3: ${System.getProperty("user.dir")}")

}

fun handleUpdateProcess(args: Array<String>) {
    val jarName = "JUpdater.jar"
    val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp")
    val jarUpdaterConfig = UpdaterConfig(arrayOf(jarName), jarName, tempDir)
    val version = AppVersion("0.1.1")
    val appInfo = AppInfo("SlimTrade", version, "")
    val properties = ProjectProperties()
    val updater = JarUpdater("zmilla93", "JUpdate", version, jarUpdaterConfig)
    updater.handleUpdateProcess(args)
    if (updater.wasJustUpdated()) return
    updater.startUpdateProcess()
}