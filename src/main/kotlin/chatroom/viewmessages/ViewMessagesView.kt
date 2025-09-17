package chatroom.viewmessages

interface ViewMessagesView {
    fun show(state: ViewMessagesViewState)
    fun getWidth() : Int
}
