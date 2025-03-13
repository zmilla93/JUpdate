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

    override fun download(): Boolean {
        if (github.latestRelease() == null) return false
        val assetName = "SlimTrade.jar"
        val asset = github.latestRelease()!!.findAssetByName(assetName)
        if (asset == null) return false
        println("temp dir: " + updaterConfig.tempDirectory)
        return github.downloadFile(
            asset.browser_download_url,
            Paths.get(updaterConfig.tempDirectory, assetName).toString()
        )
    }

    override fun unpack(): Boolean {
        TODO("Not yet implemented")
    }

    override fun patch(): Boolean {
//        updaterConfig.patchTarget
    }

    override fun clean(): Boolean {
        TODO("Not yet implemented")
    }

}