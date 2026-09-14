package chatserver.messages

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import chatserver.MessageResult

class MessageCoordinator(
    private val repository: SupabaseMessageRepository,
    appScope: CoroutineScope
) {
    // This creates single, hot multicast connection hook
    val messageStream: SharedFlow<MessageResult> = repository.observe()
    .shareIn(
        scope = appScope,
        // Opens connection on the first subscriber.
        // Disconnects from Supabase 5 seconds after the last window stops listening.
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        // Replay 1 ensures immediate UI data rendering upon opening a new window
        replay = 1
    )
}
