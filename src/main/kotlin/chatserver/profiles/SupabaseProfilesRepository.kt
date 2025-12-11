package chatserver.profiles

import chatserver.ProfileResult
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseProfilesRepository(
    private val client: SupabaseClient,
    private val scope: CoroutineScope,
): ReadChatRepository<ProfileResult>, WriteChatRepository<String>, SubscribeChatRepository {
    private val _profiles: MutableStateFlow<ProfileResult> = MutableStateFlow(ProfileResult.ok(emptyMap()))
    override fun latest(): ProfileResult = _profiles.value

    override fun observe(): Flow<ProfileResult> = _profiles.asStateFlow()

    override fun write(item: String) {
        TODO("Not yet implemented")
    }

    override fun subscribe() {
        client.from("profiles")
            .selectAsFlow(Profile::id)
            .map { it.associateBy(Profile::id)   }
    }

    override fun unsubscribe() {
        scope.cancel()
    }
    @Serializable
    data class Profile(
        @SerialName("id") val id: String,
        @SerialName("username") val username: String,
    )
}
