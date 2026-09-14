package chatserver.messages

import arch.RokyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val chatServerMessagesModule =
    module {
        single { LocalChatMessages(get()) }

        // 1. Cleanly initialize the stateless repository with just the SupabaseClient (get())
        single { SupabaseMessageRepository(get()) }

        // 2. Register the central MessageCoordinator as a shared Singleton instance
        single {
            MessageCoordinator(
                repository = get<SupabaseMessageRepository>(),
                appScope = CoroutineScope(
                    SupervisorJob() + get<RokyDispatchers>().default,
                )
            )
        }
    }
