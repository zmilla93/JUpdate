package io.github.zmilla93.jupdate

import updater.UpdateUtil
import java.net.URISyntaxException

abstract class AbstractUpdater {

    /** Returns true when a new update is available. */
    abstract fun isUpdateAvailable(): Boolean

    @JvmField
    /** This flag MUST be set to true when running the 'clean' step! */
    protected var wasJustUpdated: Boolean = false

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