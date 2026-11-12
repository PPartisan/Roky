package chatroom.sendmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.sendmessages.SendMessageEvent.SendMessage
import chatroom.sendmessages.SendMessageEvent.TextChange
import chatroom.sendmessages.SendMessageViewState.Clear
import chatroom.sendmessages.SendMessageViewState.TextLimitReached
import chatserver.WriteChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SendMessagePresenter(
    private val windowScope: CoroutineScope,
    private val message: WriteChatRepository<String>,
    private val maxChars: Int = 128,
    dispatchers: RokyDispatchers,
) : Presenter<SendMessagesView>(dispatchers) {
    override fun onAttach(view: SendMessagesView) {
        view.show(Clear)
    }

    override fun onDetach(view: SendMessagesView) {
        // Deliberately empty.
    }

    fun onEvent(event: SendMessageEvent) {
        when (event) {
            is SendMessage ->
                windowScope.launch(dispatchers.io) {
                    message.write(event.message)
                    withContext(dispatchers.main) {
                        withView { it.show(Clear) }
                    }
                }
            is TextChange -> {
                val truncated = event.text.take(maxChars)
                withView { it.show(TextLimitReached(truncated)) }
            }
        }
    }
}
