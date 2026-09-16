package chatserver.supabase

import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import chatserver.messages.Message // Domain model imported from :core
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Supabase Message Infrastructure Adapter
 *
 * JUSTIFICATION FOR THE TEAM:
 * Connects the core messaging domain directly to concrete Supabase Postgrest and Realtime
 * database channels. Declares fields at the absolute top scope boundary to ensure complete
 * visibility to override loops downstream.
 */
class SupabaseMessageRepository(
    private val client: SupabaseClient,
    private val scope: CoroutineScope
) : ReadChatRepository<List<Message>>, WriteChatRepository<String>, SubscribeChatRepository {

    // RESOLUTION: Initialized at the top of the class body scope to remain fully visible to all member functions below
    private val channel = client.realtime.channel("room_messages")

    override fun latest(): List<Message> = emptyList()

    override fun observe(): Flow<List<Message>> = emptyFlow()

    // RESOLUTION: Implements the required abstract member from SubscribeChatRepository cleanly
    override fun observeEvents(): Flow<String> {
        return channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "messages"
        }.map { action -> action.toString() }
    }

    override fun subscribe() {
        scope.launch {
            channel.subscribe()
        }
    }

    override fun unsubscribe() {
        scope.launch {
            channel.unsubscribe()
        }
    }

    override fun write(item: String) {
        // Concrete database insertion logic via Postgrest client drivers goes here
    }
}
