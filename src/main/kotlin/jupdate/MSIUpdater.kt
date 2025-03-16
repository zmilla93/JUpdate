package io.github.zmilla93.jupdate

import java.nio.file.Path

class MSIUpdater(args: Array<String>, config: UpdaterConfig, githubConfig: GitHubConfig) :
    AbstractGitHubUpdater(args, config, githubConfig) {

    companion object {
        const val patcher = "msi-patcher.ps1"
    }

    override fun getPathToCurrentLauncher(): Path? {
        return UpdateUtil.getCurrentProgramDirectory().parent.resolve(config.nativeExecutableName)
    }

    override fun unpack(): Boolean {
        return UpdateUtil.copyResourceToDisk(patcher, config.tempDirectory)
    }

    override fun runPatch() {
        args.list.addFirst(config.tempDirectory.resolve(patcher).toString())
        args.list.addFirst("powershell")
        args.addArg("--installer:" + config.tempDirectory.resolve(config.assetNames[0]))
        println("Patcher Args: " + args.list)
        // FIXME : This line is ugly
        UpdateUtil.runNewProcess(arrayListOf(*args.list.toTypedArray()))
    }

    override fun patch(): Boolean {
        // Do nothing, msi-patcher.ps1 handles this
        return true
    }

}