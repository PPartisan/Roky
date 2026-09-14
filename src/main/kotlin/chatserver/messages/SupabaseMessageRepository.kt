package chatserver.messages

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collect
import chatserver.MessageResult
import chatserver.Message
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import kotlinx.serialization.Serializable
import java.lang.Exception

// Internal table data transfer object to map Supabase fields cleanly
@Serializable
private data class SupabaseMessage(
    val id: Int = 0,
    val userId: String = "",
    val message: String = ""
)

// 1. Remove CoroutineScope from the constructor dependencies
class SupabaseMessageRepository(
    private val client: SupabaseClient
) : ReadChatRepository<MessageResult>, SubscribeChatRepository, WriteChatRepository<String> {

    private var channelJob: Job? = null

    // 2. Fulfill ReadChatRepository exactly as defined by the project interfaces
    override fun latest(): MessageResult = MessageResult.ok(emptyList())

    // 3. Fulfill WriteChatRepository explicitly matching the non-suspending method block
    override fun write(item: String) {
        val messagePayload = SupabaseMessage(userId = "Me", message = item)

        @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
        GlobalScope.launch {
            client.postgrest.from("messages").insert(messagePayload)
        }
    }

    // 4. Return a clean, cold flow. Remove explicit caching, .onEach, and .launchIn
    @OptIn(io.github.jan.supabase.annotations.SupabaseExperimental::class)
    override fun observe(): Flow<MessageResult> {
        return client.postgrest.from("messages")
        .selectAsFlow(SupabaseMessage::id)
        .map { supabaseMessages ->
            val messages = supabaseMessages.map { Message(it.userId, it.message) }
            MessageResult.ok(messages)
        }
        .catch { exception ->
            // Safely emit the error downstream using the project's standardized companion fail signature
            emit(MessageResult.fail(Exception(exception.message ?: "Unknown database error")))
        }
    }

    // 5. Fulfill SubscribeChatRepository side-effect constraints
    override fun subscribe() {
        @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
        channelJob = GlobalScope.launch {
            observe().collect()
        }
    }

    override fun unsubscribe() {
        channelJob?.cancel()
    }
}
