package chatserver.profiles

import arch.RokyDispatchers
import chatserver.ProfileResult
import chatserver.ProfileResult.Companion.ok
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import chatserver.messages.LocalChatMessages
import chatserver.profiles.SupabaseProfilesRepository.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LocalProfilesRepository(
    private val dispatchers: RokyDispatchers,
    private val scope: CoroutineScope,
) : ReadChatRepository<ProfileResult>, WriteChatRepository<String> {

    private val manualWrites: MutableStateFlow<ProfileResult> = MutableStateFlow(ProfileResult.ok(emptyMap()))

    private val source: Flow<ProfileResult> = flow {
        while (true) {
            val users = LocalChatMessages.sampleUsers.shuffled()
            emit(users.associateWith { Profile(it, it) }.let(ProfileResult::ok))
            delay(5.seconds)
        }
    }

    private val events: StateFlow<ProfileResult> = combine(source, manualWrites) { src, manual ->
        val current = src.item.toMutableMap()
        current.putAll(manual.item)
        ok(current)
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ok(emptyMap())
    )

    override fun latest(): ProfileResult = events.replayCache.firstOrNull() ?: ok(emptyMap())

    override fun observe(): Flow<ProfileResult> = events

    override fun write(item: String) {
        scope.launch {
            try {
                require(item.isNotBlank()) { "Username cannot be empty." }
                delay(2.seconds)
                check(item.isValidUsername()) { "Could not assign current username." }
                val profiles = latest().item.toMutableMap()
                profiles[item] = Profile(item, item)
                manualWrites.update { ok(profiles) }
            } catch (e: Exception) {
                manualWrites.update { ProfileResult.fail(e) }
            }
        }
    }

    companion object {
        private const val VALID_LENGTH = 6

        private fun String.isValidUsername(): Boolean = length < VALID_LENGTH
    }
}
