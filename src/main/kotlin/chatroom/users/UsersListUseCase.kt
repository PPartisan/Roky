package chatroom.users

import chatroom.viewmessages.ViewMessagesUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class UsersListUseCase {
    operator fun invoke(): Flow<List<String>> =
        flow {
            while (true) {
                val size = Random.nextInt(4, ViewMessagesUseCase.sampleUsers.size - 1)
                val list = (2..size).map { ViewMessagesUseCase.sampleUsers.random() }.distinct().sorted()
                emit(list)
                delay(10.seconds)
            }
        }
}
