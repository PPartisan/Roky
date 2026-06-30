package chatroom.viewmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.viewmessages.ViewMessagesViewState.Messages
import chatroom.viewmessages.ViewMessagesViewState.NoMessages
import chatserver.MessageResult
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewMessagesPresenter(
    private val windowScope: CoroutineScope,
    private val read: ReadChatRepository<MessageResult>,
    private val channel: SubscribeChatRepository,
    dispatchers: RokyDispatchers,
) : Presenter<ViewMessagesView>(dispatchers) {
    override fun onAttach(view: ViewMessagesView) {
        view.show(NoMessages)
        windowScope.launch(dispatchers.io) {
            read.observe()
                .filter { it.isOk }
                .map { it.item.lastOrNull()?.let { "${it.userId}: ${it.message}" }.orEmpty() }
                .map(::Messages)
                .collect { message ->
                    withContext(dispatchers.main) {
                        withView { it.show(message) }
                    }
                }
        }
        channel.subscribe()
    }

    override fun onDetach(view: ViewMessagesView) {
        channel.unsubscribe()
    }
}
