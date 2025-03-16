package io.github.zmilla93.updater.core

class MSIUpdater(args: Array<String>, config: UpdaterConfig, githubConfig: GitHubConfig) :
    GitHubUpdater(args, config, githubConfig) {

    companion object {
        const val patcher = "msi-patcher.ps1"
    }

    override fun unpack(): Boolean {
        return UpdateUtil.copyResourceToDisk(patcher, config.tempDirectory)
    }

    override fun runPatch() {
        val launcher = getNativeLauncherPathIfExists()
        if (launcher == null) {
            logger.error("Native launcher doesn't exist, program will not be patched: ${getNativeLauncherPath()}")
            return
        }
        args.list.addFirst(config.tempDirectory.resolve(patcher).toString())
        args.list.addFirst("powershell")
        args.addArg("--installer:" + config.tempDirectory.resolve(config.assetNames[0]))
        args.addArg("--launcher:" + getNativeLauncherPath())
        println("Patcher Args: " + args.list)
        // FIXME : This line is ugly
        runNewProcess(arrayListOf(*args.list.toTypedArray()))
    }

    override fun patch(): Boolean {
        // Do nothing, msi-patcher.ps1 handles this
        return true
    }

}