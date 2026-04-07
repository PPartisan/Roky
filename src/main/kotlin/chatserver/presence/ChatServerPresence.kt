package chatserver.presence

import arch.RokyDispatchers
import chatserver.messages.LocalChatMessages.Companion.sampleUsers
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val chatServerPresenceModule =
    module {
        single {
            localPresenceRepository(
                scope = CoroutineScope(SupervisorJob() + get<RokyDispatchers>().default),
                dispatchers = get<RokyDispatchers>(),
                users = {
                    with(sampleUsers) {
                        shuffled().subList(0, (4..<size).random()).toSet()
                    }
                },
            )
        }
        single {
            supabasePresenceRepository(
                client = get(),
                scope = CoroutineScope(SupervisorJob() + get<RokyDispatchers>().default),
            )
        }
    }

private fun localPresenceRepository(
    scope: CoroutineScope,
    dispatchers: RokyDispatchers,
    users: () -> Set<String>,
): LocalPresenceRepository = LocalPresenceRepository(scope, dispatchers, users)

private fun supabasePresenceRepository(
    client: SupabaseClient,
    scope: CoroutineScope,
): SupabasePresenceRepository = SupabasePresenceRepository(client, scope)
