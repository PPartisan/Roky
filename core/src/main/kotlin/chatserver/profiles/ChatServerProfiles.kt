package chatserver.profiles

import arch.RokyDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

/**
 * Pure User Profile Domain Configuration Module
 *
 * JUSTIFICATION FOR THE TEAM:
 * Enforces strict architectural decoupling by isolating user metadata dependency
 * injection definitions away from explicit third-party database frameworks or cloud SDKs.
 * This guarantees that we can add new platform frontends listed on the Roadmap later.
 * Concrete repositories are instantiated with explicit thread pools and scopes to satisfy
 * compiler constructor signatures.
 */
val chatServerProfilesModule = module {

    // Registers the abstract local mock profile data store cleanly for runtime lookups,
    // explicitly providing the required background coroutine execution boundaries.
    single {
        LocalProfilesRepository(
            dispatchers = get<RokyDispatchers>(),
            scope = CoroutineScope(SupervisorJob() + get<RokyDispatchers>().default)
        )
    }
}
