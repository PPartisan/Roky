package chatroom.sendmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.sendmessages.SendMessageEvent.SendMessage
import chatroom.sendmessages.SendMessageViewState.Sent
import chatserver.MessagesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SendMessagePresenter(
    private val windowScope: CoroutineScope,
    private val send: MessagesRepository.Write,
    dispatchers: RokyDispatchers,
) : Presenter<SendMessagesView>(dispatchers) {
    override fun onAttach(view: SendMessagesView) {
        view.show(Sent)
    }

    override fun onDetach(view: SendMessagesView) {
        // Deliberately empty.
    }

    fun onEvent(event: SendMessageEvent) {
        if (event is SendMessage) {
            windowScope.launch(dispatchers.io) {
                send.send(event.message)
                withContext(dispatchers.main) {
                    withView { it.show(Sent) }
                }
            }
        }
    }
}
