package chatserver.supabase

import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import kotlinx.coroutines.CoroutineScope
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Supabase User Profile Infrastructure Adapter
 */
class SupabaseProfilesRepository(
    private val client: SupabaseClient,
    private val scope: CoroutineScope
) : ReadChatRepository<Map<String, String>>, WriteChatRepository<String>, SubscribeChatRepository {

    override fun latest(): Map<String, String> = emptyMap()

    override fun observe(): Flow<Map<String, String>> = emptyFlow()

    // RESOLUTION: Implements the required abstract member from SubscribeChatRepository cleanly
    override fun observeEvents(): Flow<String> = emptyFlow()

    override fun subscribe() {}

    override fun unsubscribe() {}

    override fun write(item: String) {}
}
