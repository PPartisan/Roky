package arch

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Roky Thread Dispatcher Abstraction Port Contract
 *
 * JUSTIFICATION FOR THE TEAM:
 * Provides abstract thread execution contexts for business logic and background streaming pipelines.
 * Separates core asynchronous data routines completely from explicit user interface details,
 * ensuring the business domain layer remains blind to delivery frameworks (TUI/GUI/Mobile).
 */
interface RokyDispatchers {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
    val main: CoroutineDispatcher
}
