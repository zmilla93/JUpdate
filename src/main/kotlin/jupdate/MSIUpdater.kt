package io.github.zmilla93.jupdate

class MSIUpdater(args: Array<String>, config: UpdaterConfig, githubConfig: GitHubConfig) :
    AbstractGitHubUpdater(args, config, githubConfig) {

    companion object {
        const val patcher = "msi-patcher.ps1"
    }

    override fun unpack(): Boolean {
        return UpdateUtil.copyResourceToDisk(patcher, config.tempDirectory)
    }

    override fun runPatch() {
        UpdateUtil.runNewProcess(config.tempDirectory.resolve(patcher).toString(), args.toString())
    }

    override fun patch(): Boolean {
        // Do nothing, msi-patcher.ps1 handles this
        return true
    }

}