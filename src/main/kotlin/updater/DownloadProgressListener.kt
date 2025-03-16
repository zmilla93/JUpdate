package updater

/**
 * Callbacks for tracking download progress.
 * All callbacks are called on the EventDispatchThread.
 */
interface DownloadProgressListener {
    /** Called when a new download starts */
    fun onDownloadStart(fileName: String?)

    /** Called with a number 0-100 when the percentage changes. */
    fun onDownloadProgress(progressPercent: Int)

    /** Called when the download completes successfully */
    fun onDownloadComplete()

    /** Called if the download fails at any point. */
    fun onDownloadFailed()
}
