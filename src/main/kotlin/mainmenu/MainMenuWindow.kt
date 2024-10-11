package mainmenu

import StopApp
import com.googlecode.lanterna.gui2.*
import javax.swing.Action

class MainMenuWindow(private val window: Window = BasicWindow("Roky"),
    private val quit : StopApp): Window by window, MainMenuView {

    private val options : ActionListBox

    init {
        setHints(listOf(Window.Hint.CENTERED))
        val root = Panel(BorderLayout())

        options = ActionListBox()
        component = root
        val list = Panel(LinearLayout(Direction.VERTICAL))
        list.addComponent(options)
        root.addComponent(list)

        options.addItem("Login"){}
        options.addItem("Help"){}
        options.addItem("About"){}
        options.addItem("Quit"){quit()}
    }
    override fun show(state: MainMenuViewState) {
        TODO("Not yet implemented")
    }
}