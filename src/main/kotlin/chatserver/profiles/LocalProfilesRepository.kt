package chatserver.profiles

import arch.RokyDispatchers
import chatserver.ProfileResult
import chatserver.ProfileResult.Companion.ok
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import chatserver.messages.LocalChatMessages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LocalProfilesRepository(
    private val dispatchers: RokyDispatchers,
    private val scope: CoroutineScope,
) : ReadChatRepository<ProfileResult>, WriteChatRepository<String>, SubscribeChatRepository {
    private val state: MutableStateFlow<ProfileResult> = MutableStateFlow(ok(emptyMap()))

    override fun latest(): ProfileResult {
        return state.value
    }

    override fun observe(): Flow<ProfileResult> {
        return state.asStateFlow()
    }

    override fun subscribe() {
        scope.launch(dispatchers.default) {
            while (true) {
                val users = LocalChatMessages.sampleUsers.shuffled()
                state.value = users.associateWith { it }.let(ProfileResult::ok)
                delay(5.seconds)
            }
        }
    }

    override fun unsubscribe() {
        // deliberately empty
    }

    override fun write(item: String) {
        scope.launch {
            try {
                require(item.isNotBlank()) { "Username cannot be empty." }
                delay(2.seconds)
                check(item.isValidUsername()) { "Could not assign current username." }
                val profiles = latest().item.toMutableMap()
                profiles[item] = item
                state.value = ok(profiles)
            } catch (e: Exception) {
                state.value = ProfileResult.fail(e)
            }
        }
    }

    companion object {
        private const val VALID_LENGTH = 6

        private fun String.isValidUsername(): Boolean = length < VALID_LENGTH
    }
}
