package chatroom.viewmessages

sealed interface ViewMessagesViewState {
    data object Loading : ViewMessagesViewState {
        const val STATUS = "Loading..."
    }

    data object NoMessages : ViewMessagesViewState {
        const val STATUS = "No Messages Yet"
    }

    data class Messages(val lines: List<String>) : ViewMessagesViewState
}
