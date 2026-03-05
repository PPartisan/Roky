package chatserver.messages

import arch.RokyDispatchers
import chatserver.Message
import chatserver.MessageResult
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
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
    private val source: () -> Flow<Message> = { emitEveryThreeSeconds(sampleUsers, sampleMessages) },
) : ReadChatRepository<MessageResult>, SubscribeChatRepository, WriteChatRepository<String> {
    private var samples: Job? = null
    private val _events = MutableStateFlow(listOf<Message>())
    private val events = _events.asStateFlow()

    override fun latest(): MessageResult = MessageResult.ok(events.value)

    override fun observe(): Flow<MessageResult> = events.map { MessageResult.ok(it) }

    override fun subscribe() {
        samples =
            scope.launch {
                source().cancellable().collect { latestMessage ->
                    _events.update { allMessages ->
                        allMessages + latestMessage
                    }
                }
            }
    }

    override fun unsubscribe() {
        samples?.cancel()
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
                "Niamh"
            )

        private fun emitEveryThreeSeconds(
            users: List<String>,
            messages: List<String>,
        ): Flow<Message> = flow {
            while (true) {
                delay(3.seconds)
                emit(Message(users.random(), messages.random()))
            }
        }
    }

    override fun write(item: String) {
        _events.update { it + Message("Me", item) }
    }
}
