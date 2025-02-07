package chatroom.sendmessages

import chatroom.BorderedPanel
import chatroom.sendmessages.SendMessageViewState.Sent
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Border
import com.googlecode.lanterna.gui2.BorderLayout
import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.Panel
import view.CharacterWrapTextBox

class SendMessagePanel : Panel(BorderLayout()), BorderedPanel, SendMessagesView {
    private val message: CharacterWrapTextBox =
        CharacterWrapTextBox(
            TerminalSize(20, 3),
        )

    init {
        addComponent(message)
    }

    override fun bordered(): Border {
        return withBorder(Borders.singleLine("Send Message"))
    }

    override fun show(state: SendMessageViewState) {
        if (state is Sent) {
            message.text = ""
        }
    }
}
