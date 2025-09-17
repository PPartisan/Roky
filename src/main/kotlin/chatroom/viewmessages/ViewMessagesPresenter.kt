package chatroom.viewmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.viewmessages.ViewMessagesViewState.Messages
import chatroom.viewmessages.ViewMessagesViewState.NoMessages
import chatserver.ChatMessageResult
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.googlecode.lanterna.TerminalTextUtils.getWordWrappedText //when it wraps it doesn't add "-" when it cuts a word off so it looks weird
import utils.smartWrap

class ViewMessagesPresenter(
    private val windowScope: CoroutineScope,
    private val read: ReadChatRepository<ChatMessageResult>,
    private val channel: SubscribeChatRepository,
    dispatchers: RokyDispatchers,
) : Presenter<ViewMessagesView>(dispatchers) {
    override fun onAttach(view: ViewMessagesView) {
        view.show(NoMessages)
        windowScope.launch(dispatchers.io) {
            read.observe()
                .filter { it.isOk }
                .map { it.item }
                .map(::Messages)
                .collect { message ->
                    withContext(dispatchers.main) {
                        withView { view ->
                            val width=view.getWidth()
                            val wrapped = message.message.smartWrap(width)
                            view.show(Messages(wrapped))
                        }
                    }
                }
        }
        channel.subscribe()
    }

    override fun onDetach(view: ViewMessagesView) {
        channel.unsubscribe()
    }
}
