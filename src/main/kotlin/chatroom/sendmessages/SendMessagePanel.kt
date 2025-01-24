package chatroom.sendmessages

import chatroom.BorderedPanel
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Border
import com.googlecode.lanterna.gui2.BorderLayout
import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.TextBox

class SendMessagePanel : Panel(BorderLayout()), BorderedPanel {
    private val message: TextBox =
        TextBox(
            TerminalSize(20, 3),
        )

    init {
        addComponent(message)
    }

    override fun bordered(): Border {
        return withBorder(Borders.singleLine("Send Message"))
    }
}
