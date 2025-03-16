package io.github.zmilla93.gui

import updater.DownloadProgressListener
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JProgressBar

class ProgressFrame : JFrame("Updater"), DownloadProgressListener {

    val progressBar = JProgressBar()

    init {
        layout = BorderLayout()
        add(JLabel("Downloading latest version..."), BorderLayout.NORTH)
        add(progressBar, BorderLayout.SOUTH)
        minimumSize = Dimension(400, 10)
        pack()
        isVisible = true
        isAlwaysOnTop = true
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