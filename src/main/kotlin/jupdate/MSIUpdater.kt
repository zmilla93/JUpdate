package io.github.zmilla93.jupdate

import updater.data.AppVersion

class MSIUpdater(
    author: String,
    repo: String,
    currentVersion: AppVersion,
    config: GitHubUpdaterConfig,
    allowPreReleases: Boolean = false
) : AbstractGitHubUpdater(author, repo, currentVersion, config, allowPreReleases) {

    override fun unpack(): Boolean {
        TODO("Not yet implemented")
    }

    override fun patch(): Boolean {
        TODO("Not yet implemented")
    }

}