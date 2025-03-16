package io.github.zmilla93.jupdate

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

class JarUpdater(args: Array<String>, config: UpdaterConfig, githubConfig: GitHubConfig) :
    AbstractGitHubUpdater(args, config, githubConfig) {

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
        newArgs.add(launcherPathArg!!)
        args.addArg("patch")
        newArgs.addAll(args.list)
        // TODO @important: Unlock
        val processBuilder = ProcessBuilder(newArgs)
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
        processBuilder.start()
        exitProcess(0)
    }

    override fun patch(): Boolean {
        try {
//            Files.delete(Paths.get(launcherPath!!))
            println("Copying files...")
            Files.copy(
                config.tempDirectory.resolve(config.patcherFileName),
                Paths.get(launcherPath!!),
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