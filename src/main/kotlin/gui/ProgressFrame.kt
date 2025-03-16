package io.github.zmilla93.gui

import io.github.zmilla93.updater.listening.DownloadProgressListener
import updater.data.AppVersion
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JProgressBar

class ProgressFrame(newVersion: AppVersion) : JFrame("Updater"), DownloadProgressListener {

    val progressBar = JProgressBar()
//    val label =

    init {
        layout = BorderLayout()
        val topPanel = JPanel(GridBagLayout())
        topPanel.add(JLabel("Downloading version $newVersion..."))
        add(topPanel, BorderLayout.NORTH)
        add(progressBar, BorderLayout.SOUTH)
        minimumSize = Dimension(400, 10)
        pack()
        isVisible = true
        isAlwaysOnTop = true
    }

    override fun onDownloadStart(fileName: String) {
    }

    override fun onDownloadProgress(progressPercent: Int) {
        progressBar.value = progressPercent
    }

    override fun onDownloadComplete() {

    }

    override fun onDownloadFailed() {

    }

}