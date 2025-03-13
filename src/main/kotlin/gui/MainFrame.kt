package io.github.zmilla93.gui

import java.awt.Dimension
import javax.swing.JFrame

class MainFrame : JFrame() {

    init {
        title = "JUpdaterApp"
        pack()
        size = Dimension(400, 400)
        defaultCloseOperation = EXIT_ON_CLOSE
    }

}