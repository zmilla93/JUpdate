package io.github.zmilla93.jupdate

import io.github.zmilla93.updater.UpdateStep
import org.slf4j.LoggerFactory
import updater.UpdateUtil
import java.net.URISyntaxException

abstract class AbstractUpdater {

    /** Returns true when a new update is available. */
    abstract fun isUpdateAvailable(): Boolean

    /** Path to the originally running program */
    var launcherPath: String? = null
    var isLauncher = false;
    var currentUpdateStep = UpdateStep.NONE
    private var wasJustUpdated = false
    protected val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        const val LAUNCHER_PREFIX = "launcher:"
    }

    fun handleUpdateProcess(args: Array<String>) {
        val launcherArg = args.find { it.startsWith(LAUNCHER_PREFIX) }
        if (launcherArg != null) launcherPath = launcherArg.replaceFirst(LAUNCHER_PREFIX, "")
        else {
            launcherPath = getLaunchPath()
            isLauncher = true
        }
        if (launcherPath == null) return
        currentUpdateStep = getCurrentUpdateStep(args)
        when (currentUpdateStep) {
            UpdateStep.NONE -> {}
            UpdateStep.DOWNLOAD -> download()
            UpdateStep.UNPACK -> unpack()
            UpdateStep.PATCH -> {
                patch()
                runClean()
            }

            UpdateStep.CLEAN -> {
                wasJustUpdated = true
                clean()
            }
        }
    }

    /** True if the currently running program was launched after running the auto updater */
    fun wasJustUpdated(): Boolean {
        return wasJustUpdated
    }

    /**
     * Step 1/4: Downloads the new file(s) to be installed.
     */
    abstract fun download(): Boolean

    /**
     * Step 2/4: Handles and preprocessing on the newly downloaded files, like unzipping.
     * Might also do nothing.
     */
    abstract fun unpack(): Boolean

    /** Patching must run in a new process using temp files to allow overwriting of the old files. */
    abstract fun runPatch()

    /**
     * Step 3/4: Overwrite the old files with the new files. This is run as a new process,
     * typically using the temporary files, so that the original files can be overwritten.
     */
    abstract fun patch(): Boolean

    /** Cleaning must run in a new process using the newly installed files.  */
    abstract fun runClean()

    /**
     * Step 4/4: Delete any temporary files. This is run as a new process using the newly
     * installed files. This MUST set wasJustUpdated to true.
     */
    abstract fun clean(): Boolean

    private fun validateLauncherPath() {

    }

    fun getCurrentUpdateStep(args: Array<String>): UpdateStep {
        if (args.contains("patch")) return UpdateStep.PATCH
        if (args.contains("clean")) return UpdateStep.CLEAN
        return UpdateStep.NONE
    }

    /** This returns the full path to the currently running program. */
    fun getLaunchPath(): String? {
        try {
            var path = AbstractUpdater::class.java.protectionDomain.codeSource.location.toURI().path
            if (path.startsWith("/")) path = path.replaceFirst("/".toRegex(), "")
            return UpdateUtil.cleanFileSeparators(path)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return null
        }
    }

}