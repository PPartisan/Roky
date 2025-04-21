package chatroom.sendmessages

import chatroom.BorderedPanel
import chatroom.sendMessagesWidth
import chatroom.sendmessages.SendMessageEvent.SendMessage
import chatroom.sendmessages.SendMessageViewState.Clear
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Border
import com.googlecode.lanterna.gui2.BorderLayout
import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.Panel
import view.CharacterWrapTextBox

class SendMessagePanel(
    private val presenter: SendMessagePresenter,
) : Panel(BorderLayout()), BorderedPanel, SendMessagesView {
    private val message: CharacterWrapTextBox =
        CharacterWrapTextBox(
            TerminalSize(sendMessagesWidth(), 3),
        ) { presenter.onEvent(SendMessage(text)) }
    private val text: String
        get() = message.text

    init {
        addComponent(message)
        presenter.attach(this)
    }

    override fun bordered(): Border {
        return withBorder(Borders.singleLine("Send Message"))
    }

    override fun show(state: SendMessageViewState) {
        if (state is Clear) {
            message.text = ""
        }
    }
}
