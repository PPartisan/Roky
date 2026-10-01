package chatserver.presence

import chatserver.PresenceResult
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import chatserver.presence.SupabasePresenceRepository.All.Companion.toAll
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

class SupabasePresenceRepository(
    private val client: SupabaseClient,
    private val scope: CoroutineScope,
) : ReadChatRepository<PresenceResult> {

    /*
    On Observing:
        1. Create shared flow to track presence
        2. Subscribe to chatroom channel
        3. Track our current user

    When ending observe:
        1. Untrack current user
        2. Unsub from chatroom channel
        3. Shutdown shared flow
     */
    private val events: SharedFlow<PresenceResult> = with(client.channel("chatroom")) {
        val out = presenceDataFlow<Presence>()
            .map { it.map(Presence::id).toSet() }
            .map(PresenceResult::ok)
            .catch { println("Error in Presence ${it.message}") }
            .shareIn(
                scope = scope,
                started = SharingStarted.Lazily,
                replay = 1,
            )
        val me = client.auth.currentUserOrNull()?.id
        if(me != null)
            this.track(Presence(me).json)
                out
    }

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
