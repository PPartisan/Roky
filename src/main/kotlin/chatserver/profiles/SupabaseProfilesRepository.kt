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
import kotlin.coroutines.cancellation.CancellationException

class SupabaseProfilesRepository(
    private val client: SupabaseClient,
    private val userId: LoggedInUserId,
    private val scope: CoroutineScope,
) : ReadChatRepository<ProfileResult>, WriteChatRepository<String> {

    private val writeFailures = MutableSharedFlow<ProfileResult>(extraBufferCapacity = 1)

    @OptIn(SupabaseExperimental::class)
    private val supabaseEvents: Flow<ProfileResult> =
        client.from("profiles")
            .selectAsFlow(Profile::id)
            .map { ProfileResult.ok(it.associateBy(Profile::id)) }
            .catch { emit(ProfileResult.fail(RuntimeException(it))) }

    private val events: SharedFlow<ProfileResult> =
        merge(supabaseEvents, writeFailures)
            .shareIn(
                scope = scope,
                started = SharingStarted.Lazily,
                replay = 1,
            )

    override fun latest(): ProfileResult =
        events.replayCache.firstOrNull() ?: ProfileResult.ok(emptyMap())

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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                writeFailures.emit(ProfileResult.fail(e))
            }
        }
    }

    @Serializable
    data class Profile(
        @SerialName("id") val id: String,
        @SerialName("username") val username: String,
    )
}
