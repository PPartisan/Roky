package chatserver

import chatserver.messages.Message // Pure domain model class reference integration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

/**
 * Central Asynchronous Messaging Coordinator Hub
 *
 * ARCHITECTURAL IMPROVEMENT: Port Segregation Refactor
 * Instead of coupling this component to the massive 'ChatRepositories' storage container node,
 * it now strictly demands only the explicit read streaming interface it requires.
 * This completely eliminates unsafe runtime casting hazards and maximizes testability.
 */
class MessageCoordinator(
    private val messageReader: ReadChatRepository<List<Message>>,
    appScope: CoroutineScope
) {
    // RESOLUTION: Streams directly from the compile-time verified read contract port,
    // matching our clean .observe() Flow pipeline and eliminating all unsafe runtime casting hazards.
    val messageStream: SharedFlow<List<Message>> =
        messageReader.observe()
            .shareIn(
                scope = appScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
}
