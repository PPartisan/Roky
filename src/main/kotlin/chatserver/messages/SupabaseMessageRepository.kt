package chatserver.messages

import chatserver.Message
import chatserver.MessageResult
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class SupabaseMessageRepository(
    private val client: SupabaseClient,
    private val scope: CoroutineScope,
) : ReadChatRepository<MessageResult>, WriteChatRepository<String> {

    @OptIn(SupabaseExperimental::class)
    private val messages: SharedFlow<MessageResult> = client
        .from("messages")
        .selectAsFlow(SupabaseMessage::id, filter = FilterOperation("created_at", FilterOperator.GT, Clock.System.now().toString()))
        .map { supabaseMessages ->
            val messages = supabaseMessages.sortedBy { it.createdAt }.map { Message(it.profileId, it.content) }
            MessageResult.ok(messages)
        }
        .catch { println(it) }
        .shareIn(
            scope = scope,
            started = SharingStarted.Lazily,
            replay = 1
        )
    override fun latest(): MessageResult = messages.replayCache.firstOrNull()?: MessageResult.ok(emptyList())

    override fun observe(): Flow<MessageResult> = messages

    override fun write(item: String) {
        scope.launch {
            client.from("messages").insert(item.toChatMessage())
        }
    }

    private fun String.toChatMessage() =
        SupabaseMessage(
            id = UUID.randomUUID().toString(),
            profileId = client.auth.currentUserOrNull()?.id.orEmpty(),
            content = this,
            createdAt = Clock.System.now().toString(),
        )

    @Serializable
    data class SupabaseMessage(
        @SerialName("id") val id: String,
        @SerialName("profile_id") val profileId: String,
        @SerialName("content") val content: String,
        @SerialName("created_at") val createdAt: String,
    )
}
