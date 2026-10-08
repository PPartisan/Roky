package chatserver.presence

import chatserver.PresenceResult
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import chatserver.presence.SupabasePresenceRepository.All.Companion.toAll
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.time.Duration.Companion.seconds

class SupabasePresenceRepository(
    private val client: SupabaseClient,
    private val scope: CoroutineScope,
) : ReadChatRepository<PresenceResult> {
    private val events: SharedFlow<PresenceResult> = flow {
        val channel = client.channel("chatroom")

        channel.subscribe(blockUntilSubscribed = true)

        val me = client.auth.currentUserOrNull()?.id
        if (me != null) {
            channel.track(Presence(me).json)
        }

        try {
            emitAll(channel.presenceDataFlow<Presence>())
        } finally {
            withContext(NonCancellable) {
                try {
                    if (me != null) channel.untrack()
                    channel.unsubscribe()
                } catch (e: Exception) {
                    println("Error during presence teardown: ${e.message}")
                }
            }
        }
    }
        .map { it.map(Presence::id).toSet() }
        .map(PresenceResult::ok)
        .catch { println("Error in Presence ${it.message}") }
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(stopTimeout = 1.seconds),
            replay = 1,
        )

    override fun latest(): PresenceResult = events.replayCache.firstOrNull() ?: PresenceResult.ok(emptySet())

    override fun observe(): Flow<PresenceResult> = events

    private data class All(
        val joiners: Set<String>,
        val leavers: Set<String>,
    ) {
        companion object {
            fun PresenceAction.toAll() =
                All(
                    joiners = decodeJoinsAs<Presence>().map { it.id }.toSet(),
                    leavers = decodeLeavesAs<Presence>().map { it.id }.toSet(),
                )
        }
    }

    @Serializable
    data class Presence(
        @SerialName("profile_id") val id: String,
    ) {
        val json: JsonObject
            get() = Json.encodeToJsonElement(this).jsonObject
    }
}
