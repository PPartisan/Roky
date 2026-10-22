package chatroom.sendmessages

interface SendMessageViewState {
    data object Clear : SendMessageViewState
    data class TextLimitReached (val text: String) : SendMessageViewState
}
