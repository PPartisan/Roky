package chatroom.users

import chatserver.messages.LocalChatMessages
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class UsersListUseCase(
    private val users: List<String> = LocalChatMessages.sampleUsers,
    private val rndInt: () -> Int = { Random.nextInt(4, users.size - 1) },
    private val rndUser: (List<String>) -> String = { it.random() },
) {
    operator fun invoke(): Flow<List<String>> =
        flow {
            while (true) {
                val size = rndInt()
                val list = (2..size).map { rndUser(users) }.distinct().sorted()
                emit(list)
                delay(10.seconds)
            }
        }
}
