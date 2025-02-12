package chatroom.viewmessages

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.seconds

class ViewMessagesUseCase(
    private val messages: List<String> = sampleMessages,
    private val users: List<String> = sampleUsers,
) {
    operator fun invoke(): Flow<String> =
        flow {
            while (true) {
                delay(3.seconds)
                emit("${users.random()}: ${messages.random()}")
            }
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
            )
        private val sampleUsers =
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
            )
    }
}
