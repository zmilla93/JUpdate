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
        var text = "Hello, World!~ ${args.joinToString { "," }}"
        if (args.contains("clean")) text += " > Patched!!"
        if (args.contains("patch")) text += " > Updated!!"
        layout = BorderLayout()
        add(JLabel(text), BorderLayout.CENTER)
    }

}