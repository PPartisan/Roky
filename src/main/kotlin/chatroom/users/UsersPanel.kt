package chatroom.users

import chatroom.BorderedPanel
import com.googlecode.lanterna.gui2.Border
import com.googlecode.lanterna.gui2.BorderLayout
import com.googlecode.lanterna.gui2.BorderLayout.Location.CENTER
import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.Label
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.TextBox

class UsersPanel : Panel(BorderLayout()), BorderedPanel {
    private val empty = Label("Loading...").setLayoutData(CENTER)
    private val users = TextBox().setLayoutData(CENTER).setReadOnly(true)

    init {
        addComponent(empty)
        addComponent(users)
        users.isVisible = false
    }

    override fun bordered(): Border {
        return withBorder(Borders.singleLine("Users"))
    }
}
