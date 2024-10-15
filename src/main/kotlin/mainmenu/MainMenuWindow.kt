package mainmenu

import StopApp
import com.googlecode.lanterna.gui2.*
import javax.swing.Action

class MainMenuWindow(private val window: Window = BasicWindow("Roky"),
    private val presenter: MainMenuPresenter): Window by window, MainMenuView {

    private val options : ActionListBox

    init {
        setHints(listOf(Window.Hint.CENTERED))
        val root = Panel(BorderLayout())

        options = ActionListBox()
        component = root
        val list = Panel(LinearLayout(Direction.VERTICAL))
        list.addComponent(options)
        root.addComponent(list)

        presenter.attach(this)
    }
    override fun show(state: MainMenuViewState) {
        options.clearItems()
        state.rows.forEach{
            options.addItem(it.title){
                presenter.onEvent(it.event)
            }
        }
    }

    override fun close() {
        presenter.detach()
        window.close()
    }
}