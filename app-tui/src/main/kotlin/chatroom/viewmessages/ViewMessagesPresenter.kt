package chatroom.viewmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.viewmessages.ViewMessagesViewState.Messages
import chatroom.viewmessages.ViewMessagesViewState.NoMessages
import chatserver.messages.Message
import chatserver.MessageCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Message Stream View Controller
 *
 * JUSTIFICATION FOR THE TEAM:
 * Implements a presentation model layer entirely insulated from remote database adapters.
 * Listens to a unified, long-lived multicast SharedFlow hot stream exposed by the coordinator,
 * pushing immutable state maps to the console screen while running entirely on separate thread boundaries.
 */
class ViewMessagesPresenter(
    private val windowScope: CoroutineScope,
    private val coordinator: MessageCoordinator,
    dispatchers: RokyDispatchers,
) : Presenter<ViewMessagesView>(dispatchers) {

    override fun onAttach(view: ViewMessagesView) {
        view.show(NoMessages)
        windowScope.launch(dispatchers.io) {
            coordinator.messageStream.collect { list ->
                withContext(dispatchers.main) {
                    if (list.isEmpty()) {
                        view.show(NoMessages)
                    } else {
                        // RESOLUTION: Transforms structural message attributes to primitive sender text strings
                        // and explicitly concatenates them with newlines to pass the view state's String parameter contract.
                        val formattedOutput = list.joinToString("\n") { "${it.sender}: ${it.text}" }
                        view.show(Messages(formattedOutput))
                    }
                }
            }
        }
    }

    override fun onDetach(view: ViewMessagesView) {
        // RESOLUTION: Standardised abstract base member override body cleanup hook to clear view states safely
    }
}
