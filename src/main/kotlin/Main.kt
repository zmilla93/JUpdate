package io.github.zmilla93

import io.github.zmilla93.gui.MainFrame
import io.github.zmilla93.jupdate.*
import io.github.zmilla93.updater.data.DistributionType
import io.github.zmilla93.updater.data.ProjectProperties
import updater.data.AppVersion
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import javax.swing.SwingUtilities

//val updateManager: UpdateManager

val enableUpdater = false


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
    val msiPatcher = object {}.javaClass.getResourceAsStream("/msi-patcher.ps1")
    if (msiPatcher != null) {
        val text = msiPatcher.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        println(text)
        UpdateUtil.copyResourceToDisk(
            "msi-patcher.ps1",
            Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp\\test")
        )
    } else {
        System.err.println("Missing MSI Patcher!!!")
    }
}

fun handleUpdateProcess(args: Array<String>, currentVersion: AppVersion) {
    // FIXME @important : Use app name from pom

    val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp")

//    val updaterConfig =
    System.err.println("updater disabled")
    val updater = createUpdater(args, currentVersion)
    if (updater == null) System.err.println("Updater is null!")
    else {
        updater.handleCurrentlyRunningUpdate(args)
        println("Distribution Type: " + DistributionType.getTypeFromArgs(args))
        if (updater.wasJustUpdated()) {
            println("Was just updated!")
            return
        }
    }

//    if (updater.isUpdateAvailable()) updater.startUpdateProcess()
}

fun createUpdater(args: Array<String>, currentVersion: AppVersion): AbstractUpdater? {
    val jarName = "JUpdate.jar"
    val githubConfig = GitHubConfig("zmilla93", "JUpdate")
    // FIXME : TEMP DIR
    val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp")
    val jarConfig = UpdaterConfig(currentVersion, arrayOf(jarName), jarName, tempDir)
    var updater: AbstractGitHubUpdater;
    val distributionType = DistributionType.getTypeFromArgs(args)
    when (distributionType) {
        DistributionType.NONE -> return null
        DistributionType.JAR -> return JarUpdater(jarConfig, githubConfig)
        else -> return null
    }

}