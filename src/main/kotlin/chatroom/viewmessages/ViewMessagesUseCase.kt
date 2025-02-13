package chatroom.viewmessages

import chatserver.MessagesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.time.Duration.Companion.seconds

class ViewMessagesUseCase(
    private val messages: List<String> = sampleMessages,
    private val users: List<String> = sampleUsers,
    private val read: MessagesRepository.Read,
) {
    operator fun invoke(): Flow<String> {
        val rnd =
            flow {
                while (true) {
                    delay(3.seconds)
                    emit("${users.random()}: ${messages.random()}")
                }
            }
        return merge(rnd, read.observe().map { "Me: $it" })
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
                "Mike",
            )
    }
}
