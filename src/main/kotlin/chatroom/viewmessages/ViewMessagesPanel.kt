package chatroom.viewmessages

import chatroom.BorderedPanel
import com.googlecode.lanterna.gui2.Border
import com.googlecode.lanterna.gui2.BorderLayout
import com.googlecode.lanterna.gui2.BorderLayout.Location.CENTER
import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.Label
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.TextBox

class ViewMessagesPanel : Panel(BorderLayout()), BorderedPanel {
    private val empty: Label = Label("").setLayoutData(CENTER)
    private val messages: TextBox = TextBox().setLayoutData(CENTER)

    init {
        val container =
            Panel(BorderLayout()).apply {
                addComponent(empty, CENTER)
                addComponent(messages, CENTER)
            }
        addComponent(container, CENTER)
        empty.text = "Loading..."
        messages.isVisible = false
    }

    override fun bordered(): Border {
        return withBorder(Borders.singleLine("Messages"))
    }
}
