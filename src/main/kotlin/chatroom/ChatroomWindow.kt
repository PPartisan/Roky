package chatroom

import arch.WindowScope
import arch.WindowScopeProvider
import chatroom.sendmessages.SendMessagePanel
import chatroom.users.UsersPanel
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
import org.koin.core.scope.ScopeCallback
import utils.FileLogger
import view.AppWindow
import view.linearLayoutFill

class ChatroomWindow(
    menu: NavigateToMainMenu,
) : AppWindow("Chatroom", menu), KoinScopeComponent, WindowScope by WindowScopeProvider() {
    override val scope: Scope by lazy {
        createScope(this).apply {
            FileLogger.log("Koin Scope created for ChatroomWindow")

            registerCallback(
                object : ScopeCallback {
                    override fun onScopeClose(scope: Scope) {
                        FileLogger.log("Koin Scope closed for ChatroomWindow")
                    }
                },
            )
        }
    }
    private val viewMessages: ViewMessagesPanel by inject()
    private val sendMessages: SendMessagePanel by inject()
    private val usersList: UsersPanel by inject()

    init {
        setHints(listOf(CENTERED))

        val rightPanel =
            Panel(LinearLayout(VERTICAL)).apply {
                setPreferredSize(TerminalSize(RIGHT_WIDTH, TOTAL_ROWS))
                addComponent(
                    viewMessages.bordered()
                        .setPreferredSize(TerminalSize(0, CHAT_HEIGHT)).linearLayoutFill(),
                )
                addComponent(
                    sendMessages.bordered()
                        .setPreferredSize(TerminalSize(0, SEND_MESSAGES_HEIGHT)).linearLayoutFill(),
                )
            }
        val chatroom =
            Panel(LinearLayout(HORIZONTAL)).apply {
                setPreferredSize(TerminalSize(TOTAL_COLUMNS, TOTAL_ROWS))
                addComponent(usersList.bordered().setPreferredSize(TerminalSize(LEFT_WIDTH, 0)).linearLayoutFill())
                addComponent(rightPanel)
            }
        component = chatroom
    }

    override fun close() {
        FileLogger.log("Closing ChatroomWindow...")
        scope.close()
        cancel("Window closed")
        super.close()
    }
}
