package io.github.zmilla93.jupdate

import io.github.zmilla93.updater.github.GithubAPI
import updater.data.AppVersion
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.system.exitProcess

abstract class AbstractGitHubUpdater(
    author: String,
    repo: String,
    private val currentVersion: AppVersion,
    val config: GitHubUpdaterConfig,
    allowPreReleases: Boolean = false
) : AbstractUpdater() {

    val github = GithubAPI(author, repo)

    override fun isUpdateAvailable(): Boolean {
        val latestRelease = github.latestRelease() ?: return false
        val latestVersion = AppVersion(latestRelease.tag_name)
        return currentVersion != latestVersion
    }

    override fun download(): Boolean {
        if (github.latestRelease() == null) return false
        if (config.assetNames.isEmpty()) throw RuntimeException("No download targets specified.")
        for (assetName in config.assetNames) {
            logger.info("Attempting to download '$assetName'...")
            val asset = github.latestRelease()!!.findAssetByName(assetName)
            if (asset == null) {
                logger.error("Asset '$assetName' not found!")
                return false
            }
            val success = github.downloadFile(
                asset.browser_download_url,
                config.tempDirectory.resolve(assetName)
            )
            if (!success) return false
        }
        return true
    }

//    override fun unpack(): Boolean {
//        TODO("Not yet implemented")
//    }

    override fun runPatch() {
        val args = ArrayList<String>()
        // TODO @important: Add launcher
        args.add("java")
        args.add("-jar")
        args.add(config.tempDirectory.resolve(config.patcherFileName).toString())
        args.add(launcherPathArg!!)
        args.add("patch")
        // TODO @important: Unlock
        val processBuilder = ProcessBuilder(args)
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
        processBuilder.redirectOutput()
        processBuilder.start()
        exitProcess(0)
    }

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
        // TODO @important: Unlock
        val processBuilder = ProcessBuilder(args)
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
        processBuilder.start()
        exitProcess(0)
    }

    override fun clean(): Boolean {
        Files.walkFileTree(config.tempDirectory, object : SimpleFileVisitor<Path>() {
            @Throws(IOException::class)
            override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
                if (dir != null) Files.deleteIfExists(dir)
                return FileVisitResult.CONTINUE
            }

            @Throws(IOException::class)
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                Files.deleteIfExists(file)
                return FileVisitResult.CONTINUE
            }
        })
        return true
    }

}