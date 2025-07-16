package io.github.zkit4j.updater.core

import io.github.zkit4j.updater.data.AppVersion
import io.github.zkit4j.updater.github.GithubAPI
import java.nio.file.Path
import kotlin.system.exitProcess

abstract class GitHubUpdater(
    args: Array<String>,
    config: UpdaterConfig,
    githubConfig: GitHubConfig
) : Updater(args, config) {

    private val githubAPI = GithubAPI(githubConfig, downloadProgressListeners)

    companion object {
        /** Create an updater based on the distribution type. */
//        fun createUpdater(args: Array<String>, currentVersion: AppVersion): Updater? {
//            val jarName = "JUpdate.jar"
//            val msiName = "JUpdate-win-installer.msi"
//            val githubConfig = GitHubConfig("zmilla93", "JUpdate")
//            // FIXME : TEMP DIR
//            val tempDir = Paths.get("C:\\Users\\zmill\\OneDrive\\Documents\\SimStuff\\temp\\")
//            val jarConfig = UpdaterConfig(jarName, currentVersion, arrayOf(jarName), jarName, tempDir)
//            val msiConfig = UpdaterConfig("JUpdate.exe", currentVersion, arrayOf(msiName), msiName, tempDir)
//            val distributionType = DistributionType.Companion.getTypeFromArgs(args)
//            return when (distributionType) {
//                DistributionType.NONE -> null
//                DistributionType.WIN_MSI -> MSIUpdater(args, msiConfig, githubConfig)
//                DistributionType.JAR -> JarUpdater(args, jarConfig, githubConfig)
//                else -> null
//            }
//        }

    }

    override fun isUpdateAvailable(forceCheck: Boolean): Boolean {
        val latestRelease = githubAPI.latestRelease(forceCheck) ?: return false
        val latestVersion = AppVersion(latestRelease.tag_name)
        return config.currentVersion != latestVersion
    }

    override fun latestVersion(): AppVersion? {
        githubAPI.latestRelease() ?: return null
        return AppVersion(githubAPI.latestRelease()!!.tag_name)
    }

    override fun download(): Boolean {
        if (githubAPI.latestRelease() == null) return false
        if (config.assetNames.isEmpty()) throw RuntimeException("No download targets specified.")
        for (target in config.assetNames) {
            logger.info("Attempting to download '${target}'...")
            val asset = githubAPI.latestRelease()!!.findAssetByName(target)
            if (asset == null) {
                logger.error("Asset '${target}' not found!")
                return false
            }
            val success = githubAPI.downloadFile(
                asset.browser_download_url,
                config.tempDirectory.resolve(target)
            )
            if (!success) return false
        }
        return true
    }

//    override fun unpack(): Boolean {
//        TODO("Not yet implemented")
//    }

//    override fun patch(): Boolean {
////        updaterConfig.patchTarget
//        return true
//    }

    override fun runClean() {
        val argsArr = ArrayList<String>()
        // FIXME @important : Pass through program args
        val launcher: Path? = getNativeLauncherPath()
        // FIXME : Is this the best way to handle things?
        // NOTE: Having no launcher currently causes the program to crash.
        // Technically it could keep running, but it would be running from
        // the temporary directory, which could compound issues.
        if (launcher == null) {
            UpdateUtil.Companion.showErrorMessage(UpdateUtil.Companion.NO_LAUNCHER_PATH)
            exitProcess(1)
        }
        argsArr.add(getNativeLauncherPath().toString())
        argsArr.add("--clean")
        runNewProcess(argsArr)
    }

    override fun clean(): Boolean {
        // FIXME : Implement, then move to Updater base class
//        Files.walkFileTree(config.tempDirectory, object : SimpleFileVisitor<Path>() {
//            @Throws(IOException::class)
//            override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
//                if (dir != null) Files.deleteIfExists(dir)
//                return FileVisitResult.CONTINUE
//            }
//
//            @Throws(IOException::class)
//            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
//                Files.deleteIfExists(file)
//                return FileVisitResult.CONTINUE
//            }
//        })
        return true
    }

}