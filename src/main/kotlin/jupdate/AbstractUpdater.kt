package io.github.zmilla93.jupdate

import io.github.zmilla93.ArgsList
import io.github.zmilla93.updater.data.DistributionType
import io.github.zmilla93.updater.data.UpdateStep
import org.slf4j.LoggerFactory
import updater.data.AppVersion

/**
 * Abstracts the update process into 4 steps: download, unpack, patch, clean.
 * Patching happens from a temporary process, and clean is run on the newly installed
 * process, which is handled with runPatch and runClean.
 */
abstract class AbstractUpdater(argsArr: Array<String>, val config: UpdaterConfig) {

    /** Path to the originally running program */
    val args = ArgsList(argsArr)
    var launcherPath: String? = null
    var launcherPathArg: String? = null
    var currentUpdateStep = UpdateStep.NONE
    private var wasJustUpdated = false
    protected val logger = LoggerFactory.getLogger(javaClass.simpleName)
    var launchArgs = emptyArray<String>()
    var distributionType = DistributionType.NONE

    companion object {
        const val LAUNCHER_PREFIX = "--launcher:"
    }

    init {

    }

    /**
     * Starts the update process by running download, unpack, then launching the patcher.
     * Should only be called after isUpdateAvailable() returns true.
     */
    fun startUpdateProcess() {
        // TODO : App name in output?
        logger.info("Updating from version " + config.currentVersion + " to " + latestVersion() + "...")
        if (!download()) return
        if (!unpack()) return
        runPatch()
    }

    /**
     * This must be called before anything else using the program launch args.
     * The update process will continue (if running) based on the program args.
     */
    fun handleCurrentlyRunningUpdate(args: Array<String>) {
        validateLauncherPath(args)
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

    /** Returns true when a new update is available. */
    abstract fun isUpdateAvailable(): Boolean

    /** Returns the latest version from some remote API. */
    abstract fun latestVersion(): AppVersion?

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
    private fun validateLauncherPath(argsArr: Array<String>): Array<String> {
        val list = argsArr.toMutableList()
        val existingLauncherArg = ArgsList.getFullArg(argsArr, LAUNCHER_PREFIX)
        val distribution = ArgsList.getCleanArg(argsArr, DistributionType.ARG_PREFIX)
        println("found distrib:$distribution")
        distributionType = DistributionType.getType(distribution)
        if (distribution == null) {
            logger.error("No distribution type was set! This app cannot be updated automatically.")
        }
        if (args.containsArgPrefix(LAUNCHER_PREFIX)) launcherPath = args.getCleanArg(LAUNCHER_PREFIX)
        else launcherPath = UpdateUtil.getCurrentProgramPath()
        println("Launcher" + launcherPath)
//        if (existingLauncherArg != null) {
//            // Existing launch path
//            launcherPathArg = existingLauncherArg
//            launcherPath = ArgsList.cleanArg(existingLauncherArg, LAUNCHER_PREFIX)
//        } else {
//            // Use current program as launcher
//            launcherPath = UpdateUtil.getCurrentProgramPath()
//            launcherPathArg = LAUNCHER_PREFIX + launcherPath
//            list.add(launcherPathArg!!)
//            return list.toTypedArray()
//        }
        return argsArr
    }

//    private fun getFullArg(args: Array<String>, argPrefix: String): String? {
//        return args.find { it.startsWith(argPrefix) }
//    }
//
//    private fun getCleanArg(args: Array<String>, argPrefix: String): String? {
//        return cleanArg(getFullArg(args, argPrefix), argPrefix)
//    }
//
//    private fun cleanArg(arg: String?, argPrefix: String): String? {
//        if (arg == null) return null
//        return arg.replaceFirst(argPrefix, "")
//    }

}