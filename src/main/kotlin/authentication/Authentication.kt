package authentication

import arch.RokyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val authenticationModule =
    module {
        single { Auth.Factory(get(), CoroutineScope(SupervisorJob() + get<RokyDispatchers>().io)) }
        single<Auth> { get<Auth.Factory>().create() }
        single<ReadAuth> { get<Auth.Factory>().reader() }
    }
