package chatserver.presence

import chatserver.*
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
) : ReadChatRepository<PresenceResult>, SubscribeChatRepository {
    private var channel: RealtimeChannel? = null
    private val presences: MutableStateFlow<PresenceResult> = MutableStateFlow(PresenceResult.ok(setOf()))

    override fun latest(): PresenceResult = presences.value

    override fun observe(): Flow<PresenceResult> = presences.asStateFlow()

    override fun subscribe() {
        channel = client.channel("chatroom")
        channel?.presenceChangeFlow()
            ?.map { it.toAll() }
            ?.onEach { broadcast(it) }
            ?.catch { println("Error in Presence ${it.message}") }
            ?.launchIn(scope)
        scope.launch {
            channel?.subscribe(blockUntilSubscribed = true)
            val myUser = client.auth.currentUserOrNull()?.id
            if (myUser != null) {
                channel?.track(Presence(myUser).json)
            }
        }
    }

    override fun unsubscribe() {
        scope.launch {
            channel?.unsubscribe()
            channel = null
        }
    }

    private fun broadcast(all: All) =
        with(latest().item.toMutableSet()) {
            addAll(all.joiners)
            removeAll(all.leavers)
            presences.update { PresenceResult.ok(this) }
        }

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
