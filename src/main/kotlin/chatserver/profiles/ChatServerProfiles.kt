package chatserver.profiles

import arch.RokyDispatchers
import chatserver.ProfileResult
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val chatServerProfilesModule =
    module {
        single {
            LocalProfilesRepository(
                get<RokyDispatchers>(),
                CoroutineScope(
                    SupervisorJob() + get<RokyDispatchers>().default,
                ),
            )
        }
        factory<ReadChatRepository<ProfileResult>> {
            get<LocalProfilesRepository>()
        }
        factory<WriteChatRepository<String>> {
            get<LocalProfilesRepository>()
        }
        factory<SubscribeChatRepository> {
            get<LocalProfilesRepository>()
        }
    }
