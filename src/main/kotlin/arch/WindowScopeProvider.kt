package arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import utils.FileLogger
import kotlin.coroutines.cancellation.CancellationException

interface WindowScope : CoroutineScope

class WindowScopeProvider : WindowScope, CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {
    init {
        FileLogger.log("CoroutineScope created for Window")

        coroutineContext[Job]?.invokeOnCompletion { throwable ->
            when (throwable) {
                is CancellationException -> FileLogger.log("CoroutineScope cancelled normally.")
                null -> FileLogger.log("CoroutineScope completed successfully.")
                else -> FileLogger.log("CoroutineScope failed with exception: $throwable")
            }
        }
    }
}
