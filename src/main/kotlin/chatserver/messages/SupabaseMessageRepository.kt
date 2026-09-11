package chatserver.messages

import chatserver.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.FileLogger
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class SupabaseMessageRepository(
    private val client: SupabaseClient,
    private val scope: CoroutineScope,
) : ReadChatRepository<MessageResult>, WriteChatRepository<String>, SubscribeChatRepository {
    private val messages: MutableStateFlow<MessageResult> = MutableStateFlow(MessageResult.ok(emptyList()))

    private val flag = AtomicBoolean(false)

    override fun latest(): MessageResult = messages.value

    override fun observe(): Flow<MessageResult> = messages.asStateFlow()

    @OptIn(SupabaseExperimental::class)
    override fun subscribe() {
        if (flag.get())
            return
        client.from("messages")
            .selectAsFlow(SupabaseMessage::id)
            .onEach { FileLogger.log("1. BEFORE DISTINCT:\n ${it.joinToString("\n\t")}") }
            .distinct()
            .onEach { FileLogger.log("2. AFTER DISTINCT:\n ${it.joinToString("\n\t")}") }
            .map { supabaseMessages ->
                val messages = supabaseMessages.map { Message(it.id, it.profileId, it.content) }
                MessageResult.ok(messages)
            }
            .onEach { messages.value = it }
            .catch { println(it) }
            .launchIn(scope)
        flag.set(true)
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
//        scope.cancel()
    }

    @Serializable
    data class SupabaseMessage(
        @SerialName("id") val id: String,
        @SerialName("profile_id") val profileId: String,
        @SerialName("content") val content: String,
        @SerialName("created_at") val createdAt: String,
    )

    companion object {
        private fun Flow<List<SupabaseMessage>>.distinct(): Flow<List<SupabaseMessage>> = flow {
            val pastValues = mutableSetOf<String>()

            collect { values ->
                val newMessages = values.filter { pastValues.add(it.id) }
                if (newMessages.isNotEmpty()) {
                    emit(newMessages)
                }
            }
        }
    }

}
