package chatserver.presence

import arch.RokyDispatchers
import chatserver.PresenceResult
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LocalPresenceRepository(
    private val scope: CoroutineScope,
    private val dispatchers: RokyDispatchers,
    private val users: () -> Set<String>,
) : SubscribeChatRepository, ReadChatRepository<PresenceResult> {
    private val state: MutableStateFlow<PresenceResult> = MutableStateFlow(PresenceResult.ok(emptySet()))
    private var job: Job? = null

    override fun subscribe() {
        unsubscribe()
        job =
            scope.launch(dispatchers.default) {
                while (true) {
                    state.value = PresenceResult.ok(users())
                    delay(10.seconds)
                }
            }
    }

    override fun unsubscribe() {
        job?.cancel()
    }

    override fun latest(): PresenceResult = state.value

    override fun observe(): Flow<PresenceResult> = state.asStateFlow()
}
