package chatroom.sendmessages

interface SendMessageEvent {
    data class SendMessage(val message: String) : SendMessageEvent
    data class TextChange(val text: String) : SendMessageEvent
}
