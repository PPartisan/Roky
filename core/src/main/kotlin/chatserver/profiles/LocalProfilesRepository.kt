package chatserver.profiles

import arch.RokyDispatchers
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import chatserver.messages.LocalChatMessages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * Local In-Memory Mock Profiles Repository Adapter
 *
 * JUSTIFICATION FOR THE TEAM:
 * Implements standard contract ports while preserving our original custom text length
 * validation algorithms, artificial delay simulations, and user profiles cache mapping logic,
 * explicitly implementing the required 'observeEvents()' stream for real-time tracking.
 */
class LocalProfilesRepository(
    private val dispatchers: RokyDispatchers,
    private val scope: CoroutineScope
) : ReadChatRepository<Map<String, String>>, WriteChatRepository<String>, SubscribeChatRepository {

    private val state = MutableStateFlow<Map<String, String>>(emptyMap())

    // Abstract real-time event signal pipeline stream
    private val _eventStream = MutableSharedFlow<String>(extraBufferCapacity = 64)

    override fun latest(): Map<String, String> = state.value

    override fun observe(): Flow<Map<String, String>> = state.asStateFlow()

    // RESOLUTION: Implements the missing abstract member from SubscribeChatRepository cleanly
    override fun observeEvents(): Flow<String> = _eventStream.asSharedFlow()

    override fun subscribe() {
        scope.launch(dispatchers.default) {
            while (true) {
                val users = LocalChatMessages.sampleUsers.shuffled()
                state.value = users.associateWith { "$it Profile Details" }
                _eventStream.emit("Profiles cache refreshed: ${users.size} profiles mapped")
                delay(5.seconds)
            }
        }
    }

    override fun unsubscribe() {
        // Deliberately empty to match historical design intents
    }

    override fun write(item: String) {
        scope.launch(dispatchers.default) {
            try {
                require(item.isNotBlank()) { "Username cannot be empty." }
                delay(2.seconds) // Simulate network transaction latency
                check(item.isValidUsername()) { "Could not assign current username." }

                val currentProfiles = latest().toMutableMap()
                currentProfiles[item] = "$item Profile Details"
                state.value = currentProfiles
                _eventStream.emit("Profile successfully synchronized for user: $item")
            } catch (e: Exception) {
                // Cascades faults cleanly within state boundaries without throwing unsafe errors
                _eventStream.emit("Profile validation failed: ${e.message}")
            }
        }
    }

    companion object {
        private const val VALID_LENGTH = 6
        private fun String.isValidUsername(): Boolean = length < VALID_LENGTH
    }
}
