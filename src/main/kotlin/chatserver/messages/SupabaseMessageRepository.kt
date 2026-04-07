package chatserver.messages

import chatserver.Message
import chatserver.MessageResult
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

class SupabaseMessageRepository(
    private val client: SupabaseClient,
    private val scope: CoroutineScope,
) : ReadChatRepository<MessageResult>, WriteChatRepository<String>, SubscribeChatRepository {
    private val messages: MutableStateFlow<MessageResult> = MutableStateFlow(MessageResult.ok(emptyList()))

    override fun latest(): MessageResult = messages.value

    override fun observe(): Flow<MessageResult> = messages.asStateFlow()

    @OptIn(SupabaseExperimental::class)
    override fun subscribe() {
        client.from("messages")
            .selectAsFlow(SupabaseMessage::id)
            .map { supabaseMessages ->
                val messages = supabaseMessages.map { Message(it.profileId, it.content) }
                MessageResult.ok(messages)
            }
            .onEach { messages.value = it }
            .catch { println(it) }
            .launchIn(scope)
    }

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

    override fun unsubscribe() {
        scope.cancel()
    }

    @Serializable
    data class SupabaseMessage(
        @SerialName("id") val id: String,
        @SerialName("profile_id") val profileId: String,
        @SerialName("content") val content: String,
        @SerialName("created_at") val createdAt: String,
    )
}
