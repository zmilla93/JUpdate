package io.github.zmilla93.jupdate

import updater.data.AppVersion

class WindowsPortableUpdater(
    author: String,
    repo: String,
    currentVersion: AppVersion,
    updaterConfig: GitHubUpdaterConfig,
    allowPreReleases: Boolean = false
) : GitHubUpdater(author, repo, currentVersion, updaterConfig, allowPreReleases) {
}