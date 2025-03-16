package io.github.zmilla93

import io.github.zmilla93.gui.MainFrame
import io.github.zmilla93.gui.ProgressFrame
import io.github.zmilla93.updater.core.*
import io.github.zmilla93.updater.data.DistributionType
import io.github.zmilla93.updater.data.ProjectProperties
import io.github.zmilla93.updater.listening.DownloadProgressAdapter
import org.slf4j.LoggerFactory
import updater.data.AppVersion
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities


//val updateManager: UpdateManager

val enableUpdater = false
val logger = LoggerFactory.getLogger(Main::class.java.simpleName)
var progressFrame: ProgressFrame? = null

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("App Launched: ${args.joinToString(prefix = "[", postfix = "]", separator = ",")}")
            val javaVersion = System.getProperty("java.version")
            println("Java version: $javaVersion")
            val properties = ProjectProperties()
            val version = AppVersion(properties.version)
            logger.info("Current Version: " + properties.version)
            handleUpdateProcess(args, version)
            SwingUtilities.invokeLater {
                val mainFrame = MainFrame(args, version)
                mainFrame.isVisible = true
            }
        }
    }
}

fun handleUpdateProcess(args: Array<String>, currentVersion: AppVersion) {
    // FIXME @important : Use app name from pom

    val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp")

//    val updaterConfig =
    val updater = createUpdater(args, currentVersion)
    if (updater == null) {
        System.err.println("Updater is null!")
        return
    }
    updater.handleCurrentlyRunningUpdate()
    updater.addDownloadProgressListener(object : DownloadProgressAdapter() {
        override fun onDownloadProgress(progressPercent: Int) {
            println("Progress::: " + progressPercent)
        }
    })
    logger.info("Latest Version: " + updater.latestVersion())
    logger.info("Distribution Type: " + DistributionType.getTypeFromArgs(args))
    if (updater.isUpdateAvailable()) {
        SwingUtilities.invokeAndWait { progressFrame = ProgressFrame(updater.latestVersion()!!) }
        updater.addDownloadProgressListener(progressFrame!!)
        updater.startUpdateProcess()
    } else {
        logger.info("No update available.")
        updater.runPeriodicUpdateCheck(10, TimeUnit.SECONDS)
    }
    if (updater.wasJustUpdated()) {
        println("Was just updated!")
        return
    }

}

fun createUpdater(args: Array<String>, currentVersion: AppVersion): Updater? {
    val jarName = "JUpdate.jar"
    val msiName = "JUpdate-win-installer.msi"
    val githubConfig = GitHubConfig("zmilla93", "JUpdate")
    // FIXME : TEMP DIR
//    val args
    val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp\\")
    val jarConfig = UpdaterConfig(jarName, currentVersion, arrayOf(jarName), jarName, tempDir)
    val msiConfig = UpdaterConfig("JUpdate.exe", currentVersion, arrayOf(msiName), msiName, tempDir)
    var updater: GitHubUpdater
    val distributionType = DistributionType.getTypeFromArgs(args)
    when (distributionType) {
        DistributionType.NONE -> return null
        DistributionType.WIN_MSI -> return MSIUpdater(args, msiConfig, githubConfig)
        DistributionType.JAR -> return JarUpdater(args, jarConfig, githubConfig)
        else -> return null
    }

}
