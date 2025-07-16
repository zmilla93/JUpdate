package io.github.zkit4j.updater.core

/**
 * An updater that only handles alerts. A fallback for when no other updaters are available.
 */
class AlertOnlyUpdater(argsArr: Array<String>, config: UpdaterConfig, gitHubConfig: GitHubConfig) :
    GitHubUpdater(argsArr, config, gitHubConfig) {

    override val autoUpdatingSupported = false

    override fun download(): Boolean {
        return false
    }

    override fun unpack(): Boolean {
        return false
    }

    override fun runPatch() {
        // Do nothing
    }

    override fun patch(): Boolean {
        return false
    }

    override fun runClean() {
        // Do nothing
    }

    override fun clean(): Boolean {
        return false
    }

}