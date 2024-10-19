package mainmenu

import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.Window.Hint.CENTERED
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class MainMenuWindow(
    private val window: Window = BasicWindow("Roky")
): Window by window, MainMenuView, KoinScopeComponent {

    override val scope: Scope by lazy { createScope(this) }

    private val presenter : MainMenuPresenter by inject()

    private val options : ActionListBox
    private val message : Label

    init {
        setHints(listOf(CENTERED))
        val root = Panel(BorderLayout())

        options = ActionListBox()
        message = Label("Loading…").setVisible(false)

        component = root
        val list = Panel(LinearLayout(Direction.VERTICAL))
        list.addComponent(options)
        root.addComponent(list)
        root.addComponent(message)

        presenter.attach(this)
    }
    override fun show(state: MainMenuViewState) {
        options.clearItems()
        when(state) {
            is Loaded -> onLoaded(state)
            is Loading -> onLoading()
        }
    }

    private fun onLoaded(state: Loaded) {
        message.setVisible(false)
        state.rows.forEach{
            options.addItem(it.title){
                presenter.onEvent(it.event)
            }
        }
    }

    private fun onLoading() {
        message.setVisible(true)
    }

    override fun close() {
        presenter.detach()
        window.close()
        scope.close()
    }

}