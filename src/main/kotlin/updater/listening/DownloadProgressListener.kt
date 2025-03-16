package io.github.zmilla93.updater.listening

/**
 * Callbacks for tracking download progress.
 * All callbacks are called on the EDT.
 */
interface DownloadProgressListener {

    /** Called on the EDT when a new download starts */
    fun onDownloadStart(fileName: String)

    /** Called on the EDT with a number 0-100 when the percentage changes. */
    fun onDownloadProgress(progressPercent: Int)

    /** Called on the EDT when the download completes successfully */
    fun onDownloadComplete()

    /** Called on the EDT if the download fails at any point. */
    fun onDownloadFailed()

}
