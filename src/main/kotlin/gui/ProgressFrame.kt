package io.github.zmilla93.gui

import updater.DownloadProgressListener
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JProgressBar

class ProgressFrame : JFrame("Updater"), DownloadProgressListener {

    val progressBar = JProgressBar()

    init {
        layout = BorderLayout()
        add(JLabel("Updating to new version..."), BorderLayout.NORTH)
        add(progressBar, BorderLayout.SOUTH)
        pack()
    }

    override fun onDownloadStart(fileName: String?) {

    }

    override fun onDownloadProgress(progressPercent: Int) {
        progressBar.value = progressPercent
    }

    override fun onDownloadComplete() {

    }

    override fun onDownloadFailed() {

    }

}