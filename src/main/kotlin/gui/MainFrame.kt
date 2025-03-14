package io.github.zmilla93.gui

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JLabel

class MainFrame(args: Array<String>) : JFrame() {

    init {
        title = "JUpdaterApp"
        pack()
        size = Dimension(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE
        val text = "Hello, World!~ ${args}"
        if (args.contains("clean")) text + " > Updated!"
        layout = BorderLayout()
        add(JLabel(text), BorderLayout.CENTER)
    }

}