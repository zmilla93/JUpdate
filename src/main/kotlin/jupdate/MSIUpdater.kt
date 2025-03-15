package io.github.zmilla93.jupdate

class MSIUpdater(config: UpdaterConfig, githubConfig: GitHubConfig) :
    AbstractGitHubUpdater(config, githubConfig) {

    companion object {
        const val patcher = "msi-patcher.ps1"
    }

    override fun unpack(): Boolean {
        return UpdateUtil.copyResourceToDisk(patcher, config.tempDirectory)
    }

    override fun runPatch() {
        UpdateUtil.runNewProcess(config.tempDirectory.resolve(patcher).toString())
    }

    override fun patch(): Boolean {
        // Do nothing, msi-patcher.ps1 handles this
        return true
    }

}