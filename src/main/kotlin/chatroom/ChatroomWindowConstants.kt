package chatroom

import view.DEFAULT_TERMINAL_HEIGHT
import view.DEFAULT_TERMINAL_WIDTH

const val TOTAL_COLUMNS = DEFAULT_TERMINAL_WIDTH * 3
const val TOTAL_ROWS = DEFAULT_TERMINAL_HEIGHT
const val LEFT_WIDTH = 20
const val RIGHT_WIDTH = TOTAL_COLUMNS - LEFT_WIDTH
const val SEND_MESSAGES_HEIGHT = 5
const val SEND_MESSAGES_WIDTH = RIGHT_WIDTH - 7
const val VIEW_MESSAGES_WIDTH = SEND_MESSAGES_WIDTH
const val CHAT_HEIGHT = TOTAL_ROWS - SEND_MESSAGES_HEIGHT

fun sendMessagesWidth(): Int {
    assert(SEND_MESSAGES_WIDTH > 10) {
        "Send Message Width must never be less than 10 columns."
    }
    return SEND_MESSAGES_WIDTH
}
