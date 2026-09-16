package chatroom.viewmessages

import chatroom.BorderedPanel
import chatroom.viewmessages.ViewMessagesViewState.*
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Border
import com.googlecode.lanterna.gui2.BorderLayout
import com.googlecode.lanterna.gui2.BorderLayout.Location.CENTER
import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.Label
import com.googlecode.lanterna.gui2.Panel
import view.NonFocusableTextBox

class ViewMessagesPanel(
    presenter: ViewMessagesPresenter,
) : Panel(BorderLayout()), BorderedPanel, ViewMessagesView {
    private val empty: Label = Label("").setLayoutData(CENTER)
    private val messages: NonFocusableTextBox =
        NonFocusableTextBox(TerminalSize(10, 1), "")
            .setLayoutData(CENTER) as NonFocusableTextBox

    init {
        val container =
            Panel(BorderLayout()).apply {
                addComponent(empty, CENTER)
                addComponent(messages, CENTER)
            }
        addComponent(container, CENTER)
        empty.text = "Loading..."
        messages.isVisible = false
        presenter.attach(this)
    }

    override fun bordered(): Border {
        return withBorder(Borders.singleLine("Messages"))
    }

    override fun show(state: ViewMessagesViewState) {
        when (state) {
            is NoMessages -> onNoMessages(state)
            is Loading -> onLoading(state)
            is Messages -> onMessages(state)
        }
    }

    private fun onNoMessages(state: NoMessages) {
        empty.text = state.STATUS
        empty.isVisible = true
        messages.text = ""
        messages.isVisible = false
    }

    private fun onLoading(state: Loading) {
        empty.text = state.STATUS
        empty.isVisible = true
        messages.text = ""
        messages.isVisible = false
    }

    private fun onMessages(state: Messages) {
        empty.text = ""
        empty.isVisible = false
        messages.isVisible = true
        textGUI.guiThread.invokeLater {
            messages.addLineAndMaybeScrollDown(state.message)
        }
    }
}
