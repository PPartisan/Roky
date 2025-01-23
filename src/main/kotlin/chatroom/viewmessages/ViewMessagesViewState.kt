package chatroom.viewmessages

interface ViewMessagesViewState {
    data object Loading : ViewMessagesViewState {
        const val STATUS = "Loading..."
    }

    data object NoMessages : ViewMessagesViewState {
        const val STATUS = "No Messages Yet"
    }

    data class Messages(val message: String) : ViewMessagesViewState
}
