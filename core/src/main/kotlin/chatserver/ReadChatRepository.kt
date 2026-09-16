package chatserver

import kotlinx.coroutines.flow.Flow

/**
 * Pure Domain Read Stream Port Contract
 *
 * JUSTIFICATION FOR THE TEAM:
 * Defines a standardized, decoupled interface contract for pulling and observing data snapshots.
 * By keeping this contract generic and entirely free from custom monolithic wrapper objects,
 * we can cleanly map collections natively through reactive flows across all subprojects.
 */
interface ReadChatRepository<out T> {

    /**
     * Resolves the immediate, active cache snapshot present inside the volatile data repository state.
     */
    fun latest(): T

    /**
     * Exposes a non-blocking cold asynchronous data pipeline streaming real-time content changes.
     */
    fun observe(): Flow<T>
}
