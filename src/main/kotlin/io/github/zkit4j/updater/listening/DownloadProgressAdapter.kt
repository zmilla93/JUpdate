package io.github.zkit4j.updater.listening

/** Adapter class for [DownloadProgressListener] */
open class DownloadProgressAdapter : DownloadProgressListener {

    override fun onDownloadStart(fileName: String) {}

    override fun onDownloadProgress(progressPercent: Int) {}

    override fun onDownloadComplete() {}

    override fun onDownloadFailed() {}

}