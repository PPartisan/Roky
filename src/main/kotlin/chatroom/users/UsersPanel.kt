package chatroom.users

import chatroom.BorderedPanel
import chatroom.users.UsersViewState.*
import com.googlecode.lanterna.gui2.Border
import com.googlecode.lanterna.gui2.BorderLayout
import com.googlecode.lanterna.gui2.BorderLayout.Location.CENTER
import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.Label
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.TextBox

class UsersPanel(
    presenter: UsersListPresenter,
) : Panel(BorderLayout()), BorderedPanel, UsersListView {
    private val empty = Label("Loading...").setLayoutData(CENTER)
    private val users = TextBox().setLayoutData(CENTER).setReadOnly(true)

    init {
        addComponent(empty)
        addComponent(users)
        users.isVisible = false
        presenter.attach(this)
    }

    override fun bordered(): Border {
        return withBorder(Borders.singleLine("Users"))
    }

    override fun show(state: UsersViewState) {
        when (state) {
            is Empty -> onEmpty()
            is Users -> onUsers(state)
        }
    }

    private fun onUsers(state: Users) {
        empty.isVisible = false
        users.isVisible = true
        users.text = state.users.joinToString("\n")
    }

    private fun onEmpty() {
        empty.isVisible = true
        users.isVisible = false
        users.text = ""
    }

    override fun getWidth(): Int {
        return users.size.columns
    }
}
