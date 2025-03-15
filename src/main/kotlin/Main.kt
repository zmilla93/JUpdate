package io.github.zmilla93

import io.github.zmilla93.gui.MainFrame
import io.github.zmilla93.jupdate.*
import io.github.zmilla93.updater.data.DistributionType
import io.github.zmilla93.updater.data.ProjectProperties
import org.slf4j.LoggerFactory
import updater.data.AppVersion
import java.nio.file.Paths
import javax.swing.SwingUtilities

//val updateManager: UpdateManager

val enableUpdater = false
val logger = LoggerFactory.getLogger(Main::class.java.simpleName)

class Main {
    companion object {
        @JvmStatic
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
        }
    }
}

fun handleUpdateProcess(args: Array<String>, currentVersion: AppVersion) {
    // FIXME @important : Use app name from pom

    val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp")

//    val updaterConfig =
    val updater = createUpdater(args, currentVersion)
    if (updater == null) System.err.println("Updater is null!")
    else {
        updater.handleCurrentlyRunningUpdate(args)
        logger.info("Latest Version: " + updater.latestVersion())
        if (updater.isUpdateAvailable()) updater.startUpdateProcess()
        logger.info("Distribution Type: " + DistributionType.getTypeFromArgs(args))
        if (updater.wasJustUpdated()) {
            println("Was just updated!")
            return
        }
    }

//    if (updater.isUpdateAvailable()) updater.startUpdateProcess()
}

fun createUpdater(args: Array<String>, currentVersion: AppVersion): AbstractUpdater? {
    val jarName = "JUpdate.jar"
    val msiName = "JUpdate-win-portable.jar"
    val githubConfig = GitHubConfig("zmilla93", "JUpdate")
    // FIXME : TEMP DIR
//    val args
    val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp\\")
    val jarConfig = UpdaterConfig(currentVersion, arrayOf(jarName), jarName, tempDir)
    val msiConfig = UpdaterConfig(currentVersion, arrayOf(msiName), msiName, tempDir)
    var updater: AbstractGitHubUpdater
    val distributionType = DistributionType.getTypeFromArgs(args)
    when (distributionType) {
        DistributionType.NONE -> return null
        DistributionType.WIN_MSI -> return MSIUpdater(args, msiConfig, githubConfig)
        DistributionType.JAR -> return JarUpdater(args, jarConfig, githubConfig)
        else -> return null
    }

}
