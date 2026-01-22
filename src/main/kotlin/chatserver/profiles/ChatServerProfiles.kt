package chatserver.profiles

import arch.RokyDispatchers
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
        single {
            SupabaseProfilesRepository(
                get(),
                get(),
                CoroutineScope(
                    SupervisorJob() + get<RokyDispatchers>().io,
                ),
            )
        }
    }
