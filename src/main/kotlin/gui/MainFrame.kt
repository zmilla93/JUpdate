package io.github.zmilla93.gui

import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JLabel

class MainFrame(args: Array<String>) : JFrame() {

    init {
        title = "JUpdaterApp"
        pack()
        size = Dimension(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE
        var text = "Hello"
        if (args.contains("patch")) {
            text += "PATCH"
        }
        add(JLabel(text))
    }

}