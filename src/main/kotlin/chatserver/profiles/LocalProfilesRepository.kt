package chatserver.profiles

import arch.RokyDispatchers
import chatroom.viewmessages.ViewMessagesUseCase
import chatserver.ProfileResult
import chatserver.ProfileResult.Companion.ok
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
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
    private val _state: MutableStateFlow<ProfileResult> = MutableStateFlow(ok(emptyMap()))

    override fun latest(): ProfileResult {
        return _state.value
    }

    override fun observe(): Flow<ProfileResult> {
        return _state.asStateFlow()
    }

    override fun subscribe() {
        scope.launch(dispatchers.default) {
            while (true) {
                val users = ViewMessagesUseCase.sampleUsers.shuffled()
                _state.value = users.associateWith { it }.let(ProfileResult::ok)
            }
        }
    }

    override fun unsubscribe() {
    }

    override fun write(userName: String) {
        scope.launch {
            try {
                if (userName.isBlank()) {
                    throw IllegalArgumentException("Username cannot be empty.")
                }
                delay(2.seconds)
                if (!userName.isValidUsername()) {
                    throw IllegalStateException("Could not assign current username.")
                }
                val profiles = latest().item.toMutableMap()
                profiles.put(userName, userName)
                _state.value = ok(profiles)
            } catch (e: Exception) {
                _state.value = ProfileResult.fail(e)
            }
        }
    }

    companion object {
        private const val VALID_LENGTH = 6

        private fun String.isValidUsername(): Boolean = length < VALID_LENGTH
    }
}
