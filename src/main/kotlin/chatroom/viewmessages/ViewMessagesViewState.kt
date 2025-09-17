package chatroom.viewmessages

sealed interface ViewMessagesViewState {
    data object Loading : ViewMessagesViewState {
        const val STATUS = "Loading..."
    }

    data object NoMessages : ViewMessagesViewState {
        const val STATUS = "No Messages Yet"
    }

    data class Messages(val message: String) : ViewMessagesViewState
    //at the moment we split the string into a list to make the wrapping work, and then we join it again into a single
    //string with /n as separator. Then when we show the message we separate the string again in a list of strings
    //because lanterna doesn't let you addLineAndMaybeScrollDown(line) more than one line (so if you put a \n in the line
    // it's just ignored. We could then maybe have this val message already be a list of Strings to avoid this back and forth
}
