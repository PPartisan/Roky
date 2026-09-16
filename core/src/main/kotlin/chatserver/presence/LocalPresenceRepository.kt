package chatserver.presence

import arch.RokyDispatchers
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import chatserver.messages.LocalChatMessages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * Local In-Memory Mock Presence Repository Adapter
 *
 * JUSTIFICATION FOR THE TEAM:
 * Bridges core business workflows to thread-safe mock user presence tracking pools.
 * Fully preserves our original 10-second periodic shuffling presence logic engine
 * while matching our uniform multi-module pipeline signatures cleanly and implementing
 * the required 'observeEvents()' stream for tracking state.
 */
class LocalPresenceRepository(
    private val scope: CoroutineScope,
    private val dispatchers: RokyDispatchers
) : ReadChatRepository<Set<String>>, WriteChatRepository<String>, SubscribeChatRepository {

    private val state = MutableStateFlow<Set<String>>(emptySet())
    private var job: Job? = null

    // Abstract real-time event signal pipeline stream
    private val _eventStream = MutableSharedFlow<String>(extraBufferCapacity = 64)

    override fun latest(): Set<String> = state.value

    override fun observe(): Flow<Set<String>> = state.asStateFlow()

    // RESOLUTION: Implements the missing abstract member from SubscribeChatRepository cleanly
    override fun observeEvents(): Flow<String> = _eventStream.asSharedFlow()

    override fun subscribe() {
        unsubscribe()
        job = scope.launch(dispatchers.default) {
            while (true) {
                // Dynamically pull from the centralized users cache pool
                val activeUsers = LocalChatMessages.sampleUsers.shuffled().take(3).toSet()
                state.value = activeUsers
                _eventStream.emit("Presence updated: ${activeUsers.size} users online")
                delay(10.seconds)
            }
        }
    }

    override fun unsubscribe() {
        job?.cancel()
    }

    override fun write(item: String) {
        state.value = state.value + item
        scope.launch(dispatchers.default) {
            _eventStream.emit("User presence item added manually: $item")
        }
    }
}
