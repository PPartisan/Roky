package chatroom.sendmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.MockMessages
import chatroom.sendmessages.SendMessageEvent.SendMessage
import chatroom.sendmessages.SendMessageViewState.Sent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SendMessagePresenter(
    private val windowScope: CoroutineScope,
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
            // Mock end point? Multithreading.
            windowScope.launch(dispatchers.io) {
                MockMessages.publish(event.message)
                withContext(dispatchers.main) {
                    withView { it.show(Sent) }
                }
            }
        }
    }
}
