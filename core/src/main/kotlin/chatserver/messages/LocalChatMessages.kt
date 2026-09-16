package chatserver.messages

import arch.RokyDispatchers
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * Pure Kotlin Messaging Business Model
 */
data class Message(val sender: String, val text: String)

/**
 * Local In-Memory Mock Messaging Repository
 *
 * JUSTIFICATION FOR THE TEAM:
 * Implements Read, Write, and Subscribe contract ports while fully preserving our original
 * message generation ticker loop and sample database array. Replaces raw legacy object casts
 * with structural contract signatures to pass multi-module compilation checks safely.
 */
class LocalChatMessages(
    private val dispatchers: RokyDispatchers,
    private val scope: CoroutineScope
) : ReadChatRepository<List<Message>>, WriteChatRepository<String>, SubscribeChatRepository {

    private var samples: Job? = null
    private val _events = MutableStateFlow<List<Message>>(emptyList())
    private val events = _events.asStateFlow()

    // Abstract real-time event signal pipeline stream
    private val _eventStream = MutableSharedFlow<String>(extraBufferCapacity = 64)

    // RESOLUTION: Aligns precisely with your base team interface signature requirements
    override fun latest(): List<Message> = events.value

    // RESOLUTION: Aligns precisely with your base team interface signature requirements
    override fun observe(): Flow<List<Message>> = events

    // RESOLUTION: Implements the missing abstract member from SubscribeChatRepository cleanly
    override fun observeEvents(): Flow<String> = _eventStream.asSharedFlow()

    override fun subscribe() {
        samples?.cancel() // Clear down lingering routines safely
        samples = scope.launch(dispatchers.default) {
            emitEveryThreeSeconds().cancellable().collect { latestMessage ->
                _events.update { allMessages -> allMessages + latestMessage }
                _eventStream.emit("New message received from: ${latestMessage.sender}")
            }
        }
    }

    override fun unsubscribe() {
        samples?.cancel()
    }

    override fun write(item: String) {
        _events.update { allMessages -> allMessages + Message("System", item) }
        scope.launch(dispatchers.default) {
            _eventStream.emit("System write executed")
        }
    }

    /**
     * Synthetically generates incoming real-time chat traffic periodically over time.
     */
    private fun emitEveryThreeSeconds(): Flow<Message> = flow {
        while (true) {
            delay(3.seconds)
            val user = sampleUsers.random()
            val messageText = sampleMessages.random()
            emit(Message(user, messageText))
        }
    }

    companion object {
        val sampleUsers = listOf("PPartisan", "Terry", "Alex", "Jordan", "Sam")

        val sampleMessages = listOf(
            "This is a coup!",
            "What time's Roky Coding tonight?",
            "Look at the calendar...",
            "Charizard",
            "Remind me to get my washing at 4 PM",
            "ASMR....",
            "BRAIN...praise me"
        )
    }
}
