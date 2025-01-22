package chatroom.viewmessages

import chatroom.BorderedPanel
import com.googlecode.lanterna.gui2.Border
import com.googlecode.lanterna.gui2.BorderLayout
import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.Panel

class ViewMessagesPanel : Panel(BorderLayout()), BorderedPanel {
    override fun bordered(): Border {
        return withBorder(Borders.singleLine("Messages"))
    }
}
