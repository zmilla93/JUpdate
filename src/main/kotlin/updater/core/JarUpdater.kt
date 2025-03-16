package io.github.zmilla93.updater.core

import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class JarUpdater(args: Array<String>, config: UpdaterConfig, githubConfig: GitHubConfig) :
    GitHubUpdater(args, config, githubConfig) {

    override fun unpack(): Boolean {
        // Do nothing
        return true
    }

    override fun runPatch() {
        val newArgs = ArrayList<String>()
        // TODO @important: Add launcher
        newArgs.add("java")
        newArgs.add("-jar")
        newArgs.add(config.tempDirectory.resolve(config.patcherFileName).toString())
        args.addArg("--patch")
        newArgs.addAll(args.list)
        // TODO @important: Unlock
        runNewProcess(newArgs)
    }

    override fun patch(): Boolean {
        try {
            println("Copying files...")
            // FIXME : launcherNullCheck
            Files.copy(
                config.tempDirectory.resolve(config.patcherFileName),
                getNativeLauncherPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
            println("Files copied successfully!")
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

}