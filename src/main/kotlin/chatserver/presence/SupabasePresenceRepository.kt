package chatserver.presence

import chatserver.*
import chatserver.presence.SupabasePresenceRepository.All.Companion.toAll
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabasePresenceRepository(
    private val client: SupabaseClient,
    private val scope: CoroutineScope
) : ReadChatRepository<PresenceResult>, SubscribeChatRepository {
    private var _channel: RealtimeChannel? = null
    private val _presences: MutableStateFlow<PresenceResult> = MutableStateFlow(PresenceResult.ok(setOf()))

    override fun latest(): PresenceResult = _presences.value

    override fun observe(): Flow<PresenceResult> = _presences.asStateFlow()

    override fun subscribe() {
        _channel = client.channel("chatroom")
        _channel?.presenceChangeFlow()
            ?.map { it.toAll() }
            ?.onEach { broadcast(it) }
            ?.catch { println("Error in Presence ${it.message}") }
            ?.launchIn(scope)
    }

    override fun unsubscribe() {
        TODO("Not yet implemented")
    }

    private fun broadcast(all: All) = with(latest().item.toMutableSet()) {
        addAll(all.joiners)
        removeAll(all.leavers)
        _presences.update { PresenceResult.ok(this) }
    }

    private data class All(
        val joiners: Set<String>,
        val leavers: Set<String>
    ) {

        companion object {
            fun PresenceAction.toAll() = All(
                joiners = decodeJoinsAs<Presence>().map { it.id }.toSet(),
                leavers = decodeLeavesAs<Presence>().map { it.id }.toSet()
            )
        }
    }

    @Serializable
    data class Presence(
        @SerialName("profile_id") val id: String
    )

}
