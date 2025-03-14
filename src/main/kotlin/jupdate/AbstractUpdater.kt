package io.github.zmilla93.jupdate

import io.github.zmilla93.updater.data.UpdateStep
import org.slf4j.LoggerFactory

/**
 * Abstracts the update process into 4 steps: download, unpack, patch, clean.
 * Patching happens from a temporary process, and clean is run on the newly installed
 * process, which is handled with runPatch and runClean.
 */
abstract class AbstractUpdater {

    /** Returns true when a new update is available. */
    abstract fun isUpdateAvailable(): Boolean

    /** Path to the originally running program */
    var launcherPath: String? = null
    var launcherPathArg: String? = null
    var isLauncher = false;
    var currentUpdateStep = UpdateStep.NONE
    private var wasJustUpdated = false
    protected val logger = LoggerFactory.getLogger(javaClass)
    var launchArgs = emptyArray<String>()

    companion object {
        const val LAUNCHER_PREFIX = "launcher:"
    }

    /**
     * This must be called before anything else using the program launch args.
     * The update process will continue (if running) based on the program args.
     */
    fun handleCurrentlyRunningUpdate(args: Array<String>) {
        if (!validateLauncherPath(args)) return
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
    protected abstract fun download(): Boolean

    /**
     * Step 2/4: Handles and preprocessing on the newly downloaded files, like unzipping.
     * Might also do nothing.
     */
    protected abstract fun unpack(): Boolean

    /** Patching must run in a new process using temp files to allow overwriting of the old files. */
    protected abstract fun runPatch()

    /**
     * Step 3/4: Overwrite the old files with the new files. This is run as a new process,
     * typically using the temporary files, so that the original files can be overwritten.
     */
    protected abstract fun patch(): Boolean

    /** Cleaning must run in a new process using the newly installed files.  */
    protected abstract fun runClean()

    /**
     * Step 4/4: Delete any temporary files. This is run as a new process using the newly
     * installed files. This MUST set wasJustUpdated to true.
     */
    protected abstract fun clean(): Boolean

    /** Gets the current [UpdateStep] based on the program arguments. */
    private fun getCurrentUpdateStep(args: Array<String>): UpdateStep {
        if (args.contains("patch")) return UpdateStep.PATCH
        if (args.contains("clean")) return UpdateStep.CLEAN
        return UpdateStep.NONE
    }

    /**
     * Checks if an existing launcher path was supplied by the program arguments.
     * If not, set it using the currently running program.
     */
    private fun validateLauncherPath(args: Array<String>): Boolean {
        val existingLauncherArg = args.find { it.startsWith(LAUNCHER_PREFIX) }
        if (existingLauncherArg != null) {
            launcherPathArg = existingLauncherArg
            launcherPath = existingLauncherArg.replaceFirst(LAUNCHER_PREFIX, "")
        } else {
            launcherPath = UpdateUtil.getCurrentProgramPath()
            launcherPathArg = LAUNCHER_PREFIX + launcherPath;
            isLauncher = true
        }
        return launcherPath != null
    }

}