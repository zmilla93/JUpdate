package io.github.zmilla93.updater.core

import io.github.zmilla93.updater.data.ArgsList
import io.github.zmilla93.updater.data.DistributionType
import io.github.zmilla93.updater.data.UpdatePhase
import io.github.zmilla93.updater.listening.DownloadProgressListener
import io.github.zmilla93.updater.listening.UpdateCheckListener
import io.github.zmilla93.updater.listening.UpdatePhaseListener
import org.slf4j.LoggerFactory
import updater.data.AppVersion
import java.nio.file.Path
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

/**
 * Abstracts the update process into 4 steps: download, unpack, patch, clean.
 * Patching happens from a temporary process, and clean is run on the newly installed
 * process, which is handled with runPatch and runClean.
 */
abstract class Updater(argsArr: Array<String>, val config: UpdaterConfig) {

    val args = ArgsList(argsArr)

    /** Path to the originally running program */
    @Deprecated("Use native launcher path")
    var launcherPath: Path? = null

    @Deprecated("Use launcherPath and args")
    var launcherPathArg: String? = null
    var currentUpdatePhase = UpdatePhase.NONE
    private var wasJustUpdated = false
    protected val logger = LoggerFactory.getLogger(javaClass.simpleName)
    var launchArgs = emptyArray<String>()
    var distributionType = DistributionType.NONE

    /** By default, all callbacks except onProgramClose are called on the EDT. */
    var useEDT = true

    /** Thread for running delayed update checks */
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    // TODO : Add a dontRelaunchAfterUpdate parameter to allow for downloading on close
    // TODO : Add an abstracted version of (Silent, ShowProgress), but also need something for MSI and any other platform specific stuff

    // FIXME : Updater and GitHubAPI both use progress listeners, with Updater just being a passthrough. Is there a better way?
    // FIXME : Switch to generic "ProgressListener" that also passes a phase parameter
    val downloadProgressListeners = ArrayList<DownloadProgressListener>()
    val updatePhaseListeners = ArrayList<UpdatePhaseListener>()

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
    fun handleCurrentlyRunningUpdate() {
        validateLauncherPath()
        currentUpdatePhase = getCurrentUpdateStep(args)
        when (currentUpdatePhase) {
            UpdatePhase.NONE -> {}
            UpdatePhase.DOWNLOAD -> download()
            UpdatePhase.UNPACK -> unpack()
            UpdatePhase.PATCH -> {
                patch()
                runClean()
            }

            UpdatePhase.CLEAN -> {
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
    abstract fun isUpdateAvailable(forceCheck: Boolean = false): Boolean

    /** Returns the latest version from some remote API. */
    abstract fun latestVersion(): AppVersion?

    /** Returns the absolute path to the platform specific launcher file. */
    protected abstract fun getNativeLauncherPath(): Path?

    /**
     * Step 1/6: Downloads the new file(s) to be installed, storing them in the temp directory.
     * Return true on success
     */
    protected abstract fun download(): Boolean

    /**
     * Step 2/6: Handles any preprocessing on the newly downloaded files, like unzipping or creating a patcher file.
     * Might do nothing. Returns true on success.
     */
    protected abstract fun unpack(): Boolean

    /**
     * Step 3/6: Starts a new process that handles patching while terminating the current process.
     * Patching must run in a new process to allow overwriting of the original files.
     * This gets run using the "--patch" arg when using a built-in patcher.
     */
    protected abstract fun runPatch()

    /**
     * Step 4/6: Overwrites the old files with the new files.
     */
    protected abstract fun patch(): Boolean

    /** Step 5/6: Starts the newly installed process while passing the "--clean" parameter.  */
    protected abstract fun runClean()

    /**
     * Step 6/6: Deletes any temporary files, and sets the wasJustUpdated flag to true.
     */
    protected abstract fun clean(): Boolean

    /** Gets the current [UpdatePhase] based on the program arguments. */
    private fun getCurrentUpdateStep(args: ArgsList): UpdatePhase {
        if (args.containsArg("patch")) return UpdatePhase.PATCH
        if (args.containsArg("clean")) return UpdatePhase.CLEAN
        return UpdatePhase.NONE
    }

    /**
     * Checks if an existing launcher path was supplied by the program arguments.
     * If not, set it using the currently running program.
     */
    private fun validateLauncherPath() {
//        val list = argsArr.toMutableList()
//        val existingLauncherArg = ArgsList.getFullArg(argsArr, LAUNCHER_PREFIX)
//        val distribution = ArgsList.getCleanArg(argsArr, DistributionType.ARG_PREFIX)
//        distributionType = DistributionType.getType(distribution)
//        if (distribution == null) {
//            logger.error("No distribution type was set! This app cannot be updated automatically.")
//        }
        if (args.containsArgPrefix(LAUNCHER_PREFIX)) launcherPath = args.getCleanArgPath(LAUNCHER_PREFIX)
        else {
            launcherPath = getNativeLauncherPath()
            args.addArg(LAUNCHER_PREFIX + launcherPath)
        }
        logger.info("Launcher: $launcherPath()")
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
//        return argsArr
    }

    /**
     * Starts a new process then immediately terminates the currently running process.
     * If the new process handles multiple [UpdatePhase]s, pass the phase name as an
     * argument using "--phase" ("--patch" or "--clean").
     */
    fun runNewProcess(args: ArrayList<String>) {
        for (listener in updatePhaseListeners) listener.onProgramClose()
        val processBuilder = ProcessBuilder(args)
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)
        processBuilder.start()
        exitProcess(0)
    }

    /**
     * Runs an update check after a given length of time.
     * Use an [UpdateCheckListener] to listen for new update events.
     */
    fun runPeriodicUpdateCheck(delay: Int, timeUnit: TimeUnit) {
        logger.info("Scheduling an update check to run in $delay ${timeUnit}(s).")
        scheduler.schedule(Runnable {
            logger.info("Running periodic update check...")
            val update = isUpdateAvailable(true)
            if (update) {
                // FIXME @important: Switch to callback
//                FrameManager.displayUpdateAvailable(getLatestReleaseTag());
            } else {
                741
                runPeriodicUpdateCheck(delay, timeUnit)
            }
        }, delay.toLong(), timeUnit)
    }

    //
    // Listener Events
    //

    fun alertPhaseStart(updatePhase: UpdatePhase) {
        if (useEDT) SwingUtilities.invokeLater {
            for (listener in updatePhaseListeners) listener.onPhaseStart(
                updatePhase
            )
        }
        else for (listener in updatePhaseListeners) listener.onPhaseStart(updatePhase)
    }

    fun alertPhaseComplete(updatePhase: UpdatePhase) {
        if (useEDT) SwingUtilities.invokeLater {
            for (listener in updatePhaseListeners) listener.onPhaseComplete(
                updatePhase
            )
        }
        else for (listener in updatePhaseListeners) listener.onPhaseComplete(updatePhase)
    }

    //
    // Add/Remove Listeners
    //

    fun addDownloadProgressListener(listener: DownloadProgressListener) {
        downloadProgressListeners.add(listener)
    }

    fun removeDownloadProgressListener(listener: DownloadProgressListener) {
        downloadProgressListeners.remove(listener)
    }

    fun clearDownloadProgressListeners() {
        downloadProgressListeners.clear()
    }

    fun addUpdateListener(listener: UpdatePhaseListener) {
        updatePhaseListeners.add(listener)
    }

    fun removeUpdateListener(listener: UpdatePhaseListener) {
        updatePhaseListeners.remove(listener)
    }

    fun clearAllUpdateListeners() {
        updatePhaseListeners.clear()
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