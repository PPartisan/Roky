package chatserver.presence

import arch.RokyDispatchers
import chatserver.messages.LocalChatMessages.Companion.sampleUsers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val chatServerPresenceModule =
    module {
        single {
            LocalPresenceRepository(
                scope =
                    CoroutineScope(
                        SupervisorJob() + get<RokyDispatchers>().default,
                    ),
                dispatchers = get<RokyDispatchers>(),
                users = {
                    with(sampleUsers) {
                        shuffled().subList(0, (4..<size).random()).toSet()
                    }
                },
            )
        }
    }
