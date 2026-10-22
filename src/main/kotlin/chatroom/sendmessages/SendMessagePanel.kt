package chatroom.sendmessages

import chatroom.BorderedPanel
import chatroom.sendMessagesWidth
import chatroom.sendmessages.SendMessageEvent.SendMessage
import chatroom.sendmessages.SendMessageEvent.TextChange
import chatroom.sendmessages.SendMessageViewState.Clear
import chatroom.sendmessages.SendMessageViewState.TextLimitReached
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
        message.setTextChangeListener { text, isUserChange ->
            if (isUserChange)
                presenter.onEvent(TextChange(text))
        }
        presenter.attach(this)
    }

    override fun bordered(): Border {
        return withBorder(Borders.singleLine("Send Message"))
    }

    override fun show(state: SendMessageViewState) {
        when (state) {
            Clear -> {
                message.text = ""
            }
            is TextLimitReached -> {
                message.text = state.text
            }
        }
    }
}
