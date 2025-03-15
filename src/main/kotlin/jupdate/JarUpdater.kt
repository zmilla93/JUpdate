package io.github.zmilla93.jupdate

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

class JarUpdater(config: UpdaterConfig, githubConfig: GitHubConfig) : AbstractGitHubUpdater(config, githubConfig) {

    override fun unpack(): Boolean {
        // Do nothing
        return true
    }

    override fun runPatch() {
        val args = ArrayList<String>()
        // TODO @important: Add launcher
        args.add("java")
        args.add("-jar")
        args.add(config.tempDirectory.resolve(config.patcherFileName).toString())
        args.add(launcherPathArg!!)
        args.add("patch")
        // TODO @important: Unlock
        val processBuilder = ProcessBuilder(args)
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