package chatroom.viewmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.MockMessages
import chatroom.viewmessages.ViewMessagesViewState.Messages
import chatroom.viewmessages.ViewMessagesViewState.NoMessages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewMessagesPresenter(
    private val windowScope: CoroutineScope,
    private val messages: ViewMessagesUseCase,
    dispatchers: RokyDispatchers,
) : Presenter<ViewMessagesView>(dispatchers) {
    override fun onAttach(view: ViewMessagesView) {
        view.show(NoMessages)
        windowScope.launch(dispatchers.main) {
            MockMessages.events.collect {
                withView { v -> v.show(Messages("Me: $it")) }
            }
        }
        windowScope.launch(dispatchers.io) {
            messages().map { Messages(it) }.collect { message ->
                withContext(dispatchers.main) {
                    withView { it.show(message) }
                }
            }
        }
    }

    override fun onDetach(view: ViewMessagesView) {
        // deliberately empty to please the auto-formatter
    }
}
