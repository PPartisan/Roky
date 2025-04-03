package authentication

import arch.RokyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val authenticationModule =
    module {
        factory { Auth.Factory(get(), CoroutineScope(SupervisorJob() + get<RokyDispatchers>().io)) }
        factory<Auth> { get<Auth.Factory>().create() }
    }
