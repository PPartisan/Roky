package chatserver

import kotlinx.coroutines.flow.Flow

/**
 * Abstract Domain Stream Repository Port Contract
 *
 * JUSTIFICATION FOR THE TEAM:
 * Defines the core contract boundary rule for streaming incoming platform chat data.
 * Migrates old monolithic callback loop definitions to a reactive Kotlin Flow pipeline
 * to support hot-multicasting operations, allowing an unlimited number of view elements
 * to share concurrent database updates transparently through one socket session.
 */
interface SubscribeChatRepository {

    /**
     * Exposes the non-blocking real-time event pipeline streaming from the platform backend.
     */
    fun observeEvents(): Flow<String>

    fun subscribe()

    fun unsubscribe()
}
