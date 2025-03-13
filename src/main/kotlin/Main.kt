package io.github.zmilla93

import io.github.zmilla93.gui.MainFrame
import io.github.zmilla93.updater.GithubInterface
import io.github.zmilla93.updater.ProjectProperties
import updater.UpdateManager
import updater.data.AppInfo
import updater.data.AppVersion
import java.nio.file.Paths
import javax.swing.SwingUtilities

fun main() {
    println("Hello World!")
    SwingUtilities.invokeLater {
        val mainFrame = MainFrame()
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
    val github = GithubInterface("zmilla93", "SlimTrade");
    github.fetchData()
}