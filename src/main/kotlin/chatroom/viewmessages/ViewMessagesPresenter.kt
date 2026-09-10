package chatroom.viewmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.logSubscribe
import chatroom.logUnsubscribe
import chatroom.viewmessages.ViewMessagesViewState.Messages
import chatroom.viewmessages.ViewMessagesViewState.NoMessages
import chatserver.SubscribeChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewMessagesPresenter(
    private val windowScope: CoroutineScope,
    private val read: DisplayableMessages,
    private val channel: SubscribeChatRepository,
    dispatchers: RokyDispatchers,
) : Presenter<ViewMessagesView>(dispatchers) {
    override fun onAttach(view: ViewMessagesView) {
        view.show(NoMessages)
        windowScope.launch(dispatchers.io) {
            read().map(::Messages).collect { message ->
                withContext(dispatchers.main) {
                    withView { it.show(message) }
                }
            }
        }
        channel.subscribe().logSubscribe("messages")
    }

    override fun onDetach(view: ViewMessagesView) {
        channel.unsubscribe().logUnsubscribe("messages")
    }
}
