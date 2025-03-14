package io.github.zmilla93.jupdate

import io.github.zmilla93.updater.GithubAPI
import updater.data.AppVersion
import java.nio.file.Paths

abstract class GitHubUpdater(
    author: String,
    repo: String,
    private val currentVersion: AppVersion,
    val updaterConfig: GitHubUpdaterConfig,
    allowPreReleases: Boolean = false
) : AbstractUpdater() {

    val github = GithubAPI(author, repo)

    override fun isUpdateAvailable(): Boolean {
        val latestRelease = github.latestRelease() ?: return false
        val latestVersion = AppVersion(latestRelease.tag_name)
        return currentVersion != latestVersion
    }

    override fun download(vararg assetNames: String): Boolean {
        if (github.latestRelease() == null) return false
        if (assetNames.isEmpty()) throw RuntimeException("No download targets specified.")
        for (assetName in assetNames) {
            val asset = github.latestRelease()!!.findAssetByName(assetName)
            if (asset == null) return false
            val success = github.downloadFile(
                asset.browser_download_url,
                Paths.get(updaterConfig.tempDirectory, assetName).toString()
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
        args.add(Paths.get(updaterConfig.tempDirectory).resolve(updaterConfig.patchTarget).toString())
        args.add("patch")
        // TODO @important: Unlock
        val processBuilder = ProcessBuilder(args)
        processBuilder.start()
    }

    override fun patch(): Boolean {
//        updaterConfig.patchTarget
        return true
    }

    override fun runClean() {
        TODO("Not yet implemented")
    }

    override fun clean(): Boolean {
        TODO("Not yet implemented")
    }

}