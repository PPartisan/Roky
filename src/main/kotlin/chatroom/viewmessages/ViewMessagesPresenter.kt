package chatroom.viewmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.viewmessages.ViewMessagesViewState.Messages
import chatroom.viewmessages.ViewMessagesViewState.NoMessages
import chatserver.SubscribeChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.SmartWrap

class ViewMessagesPresenter(
    private val windowScope: CoroutineScope,
    private val read: DisplayableMessages,
    private val channel: SubscribeChatRepository,
    private val smartWrap: (String)->String,
    dispatchers: RokyDispatchers,
) : Presenter<ViewMessagesView>(dispatchers) {
    override fun onAttach(view: ViewMessagesView) {
        view.show(NoMessages)
        windowScope.launch(dispatchers.io) {
            read().map(smartWrap).map(::Messages).collect { message ->
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
