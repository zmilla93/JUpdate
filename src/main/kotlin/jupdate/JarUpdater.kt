package io.github.zmilla93.jupdate

import updater.data.AppVersion

class JarUpdater(
    author: String,
    repo: String,
    currentVersion: AppVersion,
    updaterConfig: GitHubUpdaterConfig,
    allowPreReleases: Boolean = false
) : GitHubUpdater(author, repo, currentVersion, updaterConfig, allowPreReleases) {

    override fun unpack(): Boolean {
        // Do nothing
        return true
    }

}