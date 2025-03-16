package io.github.zmilla93

import io.github.zmilla93.gui.MainFrame
import io.github.zmilla93.gui.ProgressFrame
import io.github.zmilla93.updater.core.*
import io.github.zmilla93.updater.data.DistributionType
import io.github.zmilla93.updater.data.ProjectProperties
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

/** Handle the currently running update process, if any, then handle initial update check.*/
fun handleUpdateProcess(args: Array<String>, currentVersion: AppVersion) {
    // FIXME @important : Use app name from pom
    val updater = createUpdater(args, currentVersion)
    if (updater == null) {
        System.err.println("Updater is null!")
        return
    }
    // FIXME : Move this to constructor
    updater.handleCurrentlyRunningUpdate()
    logger.info("Latest Version: " + updater.latestVersion())
    logger.info("Distribution Type: " + DistributionType.getTypeFromArgs(args))
    if (!updater.wasJustUpdated() && updater.isUpdateAvailable()) {
        // Start new update process
        SwingUtilities.invokeAndWait { progressFrame = ProgressFrame(updater.latestVersion()!!) }
        updater.addDownloadProgressListener(progressFrame!!)
        updater.startUpdateProcess()
    } else {
        // No update available - Run periodic update
        if (updater.wasJustUpdated()) logger.info("A new update has been installed!")
        else logger.info("No update available.")
        // FIXME: Periodic update check
        updater.runPeriodicUpdateCheck(1, TimeUnit.DAYS)
    }
}

/** Create an updater based on the distribution type. */
fun createUpdater(args: Array<String>, currentVersion: AppVersion): Updater? {
    val jarName = "JUpdate.jar"
    val msiName = "JUpdate-win-installer.msi"
    val githubConfig = GitHubConfig("zmilla93", "JUpdate")
    // FIXME : TEMP DIR
    val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp\\")
    val jarConfig = UpdaterConfig(jarName, currentVersion, arrayOf(jarName), jarName, tempDir)
    val msiConfig = UpdaterConfig("JUpdate.exe", currentVersion, arrayOf(msiName), msiName, tempDir)
    val distributionType = DistributionType.getTypeFromArgs(args)
    return when (distributionType) {
        DistributionType.NONE -> null
        DistributionType.WIN_MSI -> MSIUpdater(args, msiConfig, githubConfig)
        DistributionType.JAR -> JarUpdater(args, jarConfig, githubConfig)
        else -> null
    }

}
