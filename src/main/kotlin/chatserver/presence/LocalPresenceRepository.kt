package chatserver.presence

import arch.RokyDispatchers
import chatserver.PresenceResult
import chatserver.ReadChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LocalPresenceRepository(
    private val dispatchers: RokyDispatchers,
    private val scope: CoroutineScope = CoroutineScope(dispatchers.default + Job()),
    private val users: () -> Set<String>,
) : ReadChatRepository<PresenceResult> {

    private val events: StateFlow<PresenceResult> = flow {
        while(true) {
            emit(PresenceResult.ok(users()))
            delay(10.seconds)
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PresenceResult.ok(emptySet())
    )

    override fun latest(): PresenceResult = events.replayCache.firstOrNull()?: PresenceResult.ok(emptySet())

    override fun observe(): Flow<PresenceResult> = events
}
