package io.github.zmilla93.jupdate

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

    /**
     * Step 3/4: Overwrite the old files with the new files. This is run as a new process,
     * typically using the temporary files, so that the original files can be overwritten.
     */
    abstract fun patch(): Boolean

    /**
     * Step 4/4: Delete any temporary files. This is run as a new process using the newly
     * installed files. This MUST set wasJustUpdated to true.
     */
    abstract fun clean(): Boolean

}