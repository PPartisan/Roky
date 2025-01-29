package chatroom.viewmessages

import arch.Presenter
import arch.RokyDispatchers
import chatroom.viewmessages.ViewMessagesViewState.NoMessages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ViewMessagesPresenter(
    private val windowScope: CoroutineScope,
    dispatchers: RokyDispatchers,
) : Presenter<ViewMessagesView>(dispatchers) {
    override fun onAttach(view: ViewMessagesView) {
        view.show(NoMessages)
        windowScope.launch(dispatchers.default) {
            while (true) {
                delay(3.seconds)
                windowScope.launch(dispatchers.main) {
                    withView {
                        it.show(
                            ViewMessagesViewState.Messages(
                                "${sampleUsers.random()}: ${sampleMessages.random()}",
                            ),
                        )
                    }
                }
            }
        }
    }

    override fun onDetach(view: ViewMessagesView) {
        TODO("Not yet implemented")
    }

    companion object {
        private val sampleMessages =
            listOf(
                "This is a coup!",
                "What time's Roky Coding tonight?",
                "Look at the calendar...",
                "Charizard",
                "Remind me to get my washing at 4 PM",
            )
        private val sampleUsers =
            listOf(
                "Martine",
                "Ed",
                "Kai",
                "Terry",
                "Robert",
                "Tom",
            )
    }
}
