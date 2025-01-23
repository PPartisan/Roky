package chatroom

import arch.WindowScope
import arch.WindowScopeProvider
import chatroom.viewmessages.ViewMessagesPanel
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Direction.HORIZONTAL
import com.googlecode.lanterna.gui2.Direction.VERTICAL
import com.googlecode.lanterna.gui2.LinearLayout
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.Window.Hint.CENTERED
import kotlinx.coroutines.cancel
import navigation.NavigateToMainMenu
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import view.AppWindow
import view.DEFAULT_TERMINAL_HEIGHT
import view.DEFAULT_TERMINAL_WIDTH
import view.linearLayoutFill

class ChatroomWindow(
    menu: NavigateToMainMenu,
) : AppWindow("Chatroom", menu), KoinScopeComponent, WindowScope by WindowScopeProvider() {
    override val scope: Scope by lazy { createScope(this) }
    private val viewMessages: ViewMessagesPanel by inject()

    init {
        setHints(listOf(CENTERED))
        val totalColumns = DEFAULT_TERMINAL_WIDTH * 3
        val totalRows = DEFAULT_TERMINAL_HEIGHT
        val leftWidth = LEFT_PANEL_WIDTH
        val rightWidth = totalColumns - leftWidth
        val sendMessagesHeight = 5
        val chatHeight = totalRows - sendMessagesHeight

        val rightPanel =
            Panel(LinearLayout(VERTICAL)).apply {
                setPreferredSize(TerminalSize(rightWidth, totalRows))
                addComponent(viewMessages.bordered().setPreferredSize(TerminalSize(0, chatHeight)).linearLayoutFill())
                // add send message panel here
            }
        val chatroom =
            Panel(LinearLayout(HORIZONTAL)).apply {
                setPreferredSize(TerminalSize(totalColumns, totalRows))
                // add users panel here
                addComponent(rightPanel)
            }
        component = chatroom
    }

    override fun close() {
        super.close()
        scope.close()
        windowScope.cancel()
    }

    companion object {
        private const val LEFT_PANEL_WIDTH = 20
    }
}
