package io.github.zmilla93.jupdate

import io.github.zmilla93.updater.GithubAPI
import updater.data.AppVersion
import kotlin.system.exitProcess

abstract class GitHubUpdater(
    author: String,
    repo: String,
    private val currentVersion: AppVersion,
    val updaterConfig: UpdaterConfig,
    allowPreReleases: Boolean = false
) : AbstractUpdater() {

    val github = GithubAPI(author, repo)

    override fun isUpdateAvailable(): Boolean {
        val latestRelease = github.latestRelease() ?: return false
        val latestVersion = AppVersion(latestRelease.tag_name)
        return currentVersion != latestVersion
    }

    fun startUpdateProcess() {
        download()
        runPatch()
    }

    override fun download(): Boolean {
        if (github.latestRelease() == null) return false
        if (updaterConfig.assetNames.isEmpty()) throw RuntimeException("No download targets specified.")
        for (assetName in updaterConfig.assetNames) {
            val asset = github.latestRelease()!!.findAssetByName(assetName)
            if (asset == null) return false
            val success = github.downloadFile(
                asset.browser_download_url,
                updaterConfig.tempDirectory.resolve(assetName)
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
        args.add(updaterConfig.tempDirectory.resolve(updaterConfig.patcherFileName).toString())
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
        args.add("java")
        args.add("-jar")
        args.add(launcherPath!!)
        args.add("clean")
        // TODO @important: Unlock
        val processBuilder = ProcessBuilder(args)
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
        processBuilder.start()
        exitProcess(0)
    }

    override fun clean(): Boolean {
        TODO("Not yet implemented")
    }

}