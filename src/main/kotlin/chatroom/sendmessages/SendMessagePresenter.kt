package chatroom.sendmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.sendmessages.SendMessageEvent.SendMessage
import chatroom.sendmessages.SendMessageViewState.Clear
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SendMessagePresenter(
    private val windowScope: CoroutineScope,
    private val send:(String)->Unit = ::println,
    dispatchers: RokyDispatchers,
) : Presenter<SendMessagesView>(dispatchers) {
    override fun onAttach(view: SendMessagesView) {
        view.show(Clear)
    }

    override fun onDetach(view: SendMessagesView) {
        // Deliberately empty.
    }

    fun onEvent(event: SendMessageEvent) {
        if (event is SendMessage) {
            windowScope.launch(dispatchers.io) {
                send(event.message)
                withContext(dispatchers.main) {
                    withView { it.show(Clear) }
                }
            }
        }
    }
}
