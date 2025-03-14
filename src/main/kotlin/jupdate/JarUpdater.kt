package io.github.zmilla93.jupdate

import updater.data.AppVersion
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class JarUpdater(
    author: String,
    repo: String,
    currentVersion: AppVersion,
    updaterConfig: GitHubUpdaterConfig,
    allowPreReleases: Boolean = false
) : AbstractGitHubUpdater(author, repo, currentVersion, updaterConfig, allowPreReleases) {

    override fun unpack(): Boolean {
        // Do nothing
        return true
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