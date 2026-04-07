package chatserver.messages

import arch.RokyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val chatServerMessagesModule =
    module {
        single { LocalChatMessages(get()) }
        single {
            SupabaseMessageRepository(
                get(),
                CoroutineScope(
                    SupervisorJob() + get<RokyDispatchers>().default,
                ),
            )
        }
    }
