package io.github.zmilla93

import io.github.zmilla93.gui.MainFrame
import io.github.zmilla93.jupdate.GitHubUpdaterConfig
import io.github.zmilla93.jupdate.JarUpdater
import io.github.zmilla93.updater.ProjectProperties
import updater.UpdateManager
import updater.data.AppInfo
import updater.data.AppVersion
import java.io.File
import java.nio.file.Paths
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    println("Hello World!")
    SwingUtilities.invokeLater {
        val mainFrame = MainFrame(args)
        mainFrame.isVisible = true
    }
    val version = AppVersion("0.1.1")
    val appInfo = AppInfo("SlimTrade", version, "")
    val properties = ProjectProperties()
    println("VERSION : " + properties.version)
    val updateManger = UpdateManager(
        "zmilla93",
        "SlimTrade",
        // FIXME : Temp path
        Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp"),
        appInfo,
        false
    )


//    val updateAvailable = updateManger.isUpdateAvailable
//    println("update: " + updateAvailable)
//    val github = GithubAPI("zmilla93", "SlimTrade", false);
//    println("Latest Release: ${github.latestRelease()!!.name}")
//    println("Latest Release: ${github.latestRelease()!!.tag_name}")
    val tempDir = "C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp"
    val jarName = "JUpdater.jar"
    val jarUpdaterConfig = GitHubUpdaterConfig(jarName, jarName, tempDir)
    val updater = JarUpdater("zmilla93", "JUpdate", version, jarUpdaterConfig)
    updater.download(jarName)
    updater.runPatch()
    println("Update available: ${updater.isUpdateAvailable()}")
//    if (updater.isUpdateAvailable())
//    updater.download()
    val path = File(".").canonicalPath
    println("Launch1: ${updater.getLaunchPath()}")
    println("Launch2: $path")
    println("Launch3: ${System.getProperty("user.dir")}")
}