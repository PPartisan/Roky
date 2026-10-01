package chatserver.messages

import arch.RokyDispatchers
import chatserver.Message
import chatserver.MessageResult
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LocalChatMessages(
    private val dispatchers: RokyDispatchers,
    private val scope: CoroutineScope = CoroutineScope(dispatchers.default + Job()),
    source: () -> Flow<Message> = { emitEveryThreeSeconds() },
) : ReadChatRepository<MessageResult>, WriteChatRepository<String> {

    private val manualWrites = MutableSharedFlow<Message>(extraBufferCapacity = 64)

    private val events: StateFlow<MessageResult> = merge(source(), manualWrites)
        .runningFold(emptyList<Message>()) { allMessages, newMessage ->
            allMessages + newMessage
        }
        .map(MessageResult::ok)
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MessageResult.ok(emptyList())
        )

    override fun latest(): MessageResult = events.value

    override fun observe(): Flow<MessageResult> = events

    override fun write(item: String) {
        manualWrites.tryEmit(Message("Me", item))
    }

    companion object {
        private val sampleMessages =
            listOf(
                "This is a coup!",
                "What time's Roky Coding tonight?",
                "Look at the calendar...",
                "Charizard",
                "Remind me to get my washing at 4 PM",
                "ASMR....",
                "BRAIN...praise me",
                "Biggleswade is naff",
                "Biggleswade is amazing /s",
                "I love Biggleswade!!!",
                "AHHHHHHHHHHHHHHHHHH",
                "Wordle: 4/6",
                "Wordle: 1/6",
                "Wordle: 2/6",
                "I AM SO HUNGRY RN",
                "I am feeling quiet today",
                ":thumbs_up:",
                "I just have a bit of a cold rn",
                "I just think it's something going around",
                "Don't just type out what I'm saying Mike",
                ":breathing_noises:",
                "Wawu!!!!!",
            )

        val sampleUsers =
            listOf(
                "Martine",
                "Ed",
                "Kai",
                "Terry",
                "Robert",
                "Tom",
                "Brian",
                "Dunia",
                "Stefano",
                "Mike",
                "Niamh",
                "Chioma",
            )

        private fun emitEveryThreeSeconds(
            users: List<String> = sampleUsers,
            messages: List<String> = sampleMessages,
        ): Flow<Message> =
            flow {
                while (true) {
                    delay(3.seconds)
                    emit(Message(users.random(), messages.random()))
                }
            }
    }
}
