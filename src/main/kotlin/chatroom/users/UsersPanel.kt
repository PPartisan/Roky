package chatroom.users

import arch.RokyDispatchers
import chatroom.BorderedPanel
import com.googlecode.lanterna.gui2.Border
import com.googlecode.lanterna.gui2.BorderLayout
import com.googlecode.lanterna.gui2.BorderLayout.Location.CENTER
import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.Label
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.TextBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class UsersPanel(
    useCase: UsersListUseCase,
    scope: CoroutineScope,
    dispatcher: RokyDispatchers,
) : Panel(BorderLayout()), BorderedPanel {
    private val empty = Label("Loading...").setLayoutData(CENTER)
    private val users = TextBox().setLayoutData(CENTER).setReadOnly(true)

    init {
        addComponent(empty)
        addComponent(users)
        users.isVisible = false
        scope.launch(dispatcher.main) {
            useCase().collect {
                users.isVisible = true
                empty.isVisible = false
                users.text = it.joinToString("\n")
            }
        }
    }

    override fun bordered(): Border {
        return withBorder(Borders.singleLine("Users"))
    }
}
