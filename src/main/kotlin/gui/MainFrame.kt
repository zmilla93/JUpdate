package io.github.zmilla93.gui

import updater.data.AppVersion
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JLabel

class MainFrame(args: Array<String>, version: AppVersion) : JFrame() {

    init {
        title = "JUpdaterApp"
        pack()
        size = Dimension(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE
        var text = "Hello, World!~ ${args.joinToString { "," }}"
        if (args.contains("--clean")) text += " > Updated!! (cleaned)"
        if (args.contains("--patch")) text += " > Patched!!"
        text += " Version: $version"
        layout = BorderLayout()
        add(JLabel(text), BorderLayout.CENTER)
    }

}