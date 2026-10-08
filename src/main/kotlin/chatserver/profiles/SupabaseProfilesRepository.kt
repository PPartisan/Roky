package chatserver.profiles

import chatserver.LoggedInUserId
import chatserver.ProfileResult
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseProfilesRepository(
    private val client: SupabaseClient,
    private val userId: LoggedInUserId,
    private val scope: CoroutineScope,
) : ReadChatRepository<ProfileResult>, WriteChatRepository<String> {

    @OptIn(SupabaseExperimental::class)
    private val events: SharedFlow<ProfileResult> = client.from("profiles")
        .selectAsFlow(Profile::id)
        .map { it.associateBy(Profile::id) }
        .map { ProfileResult.ok(it) }
        .catch { println(it) }
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )


    override fun latest(): ProfileResult = events.replayCache.firstOrNull() ?: ProfileResult.ok(emptyMap())

    override fun observe(): Flow<ProfileResult> = events

    override fun write(item: String) {
        scope.launch {
            try {
                client.from("profiles")
                    .update({
                        set("username", item)
                    }) {
                        filter {
                            eq("id", userId())
                        }
                    }
            } catch (e: Exception) {
                println("Exception occurred while sending item. Exception $e")
                //ToDo - Push exception onto queue?
//                profiles.value = ProfileResult.fail(e)
            }
        }
    }

    @Serializable
    data class Profile(
        @SerialName("id") val id: String,
        @SerialName("username") val username: String,
    )
}
