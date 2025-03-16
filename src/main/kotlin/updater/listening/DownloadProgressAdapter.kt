package io.github.zmilla93.updater.listening

open class DownloadProgressAdapter : DownloadProgressListener {

    override fun onDownloadStart(fileName: String) {}

    override fun onDownloadProgress(progressPercent: Int) {}

    override fun onDownloadComplete() {}

    override fun onDownloadFailed() {}

}