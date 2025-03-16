package io.github.zmilla93.jupdate

import io.github.zmilla93.updater.github.GithubAPI
import updater.data.AppVersion

abstract class AbstractGitHubUpdater(
    args: Array<String>,
    config: UpdaterConfig,
    githubConfig: GitHubConfig
) : AbstractUpdater(args, config) {

    private val githubAPI = GithubAPI(githubConfig)

    override fun isUpdateAvailable(): Boolean {
        val latestRelease = githubAPI.latestRelease() ?: return false
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
        val args = ArrayList<String>()
        // TODO @important: Add launcher
        println("Running clean....")
        println("launcher: $launcherPath")
        args.add("java")
        args.add("-jar")
        args.add(launcherPath!!)
        args.add(launcherPathArg!!)
        args.add("clean")
        UpdateUtil.runNewProcess(args)
        // TODO @important: Unlock
//        val processBuilder = ProcessBuilder(args)
//        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
//        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
//        processBuilder.start()
//        exitProcess(0)
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