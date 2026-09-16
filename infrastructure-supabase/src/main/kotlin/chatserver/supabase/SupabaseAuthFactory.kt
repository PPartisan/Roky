package chatserver.supabase

import authentication.Auth // Imports our pure domain contract ports from :core
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

/**
 * Infrastructure Platform Authentication Factory & Registry
 *
 * JUSTIFICATION FOR THE TEAM:
 * Encapsulates credentials routing safely within the infrastructure module.
 * Resolves the runtime authentication implementation dynamically based on properties
 * configured at the root initialization entry point.
 */
val infrastructureAuthModule = module {
    single<Auth> {
        val useLocalMocks: String = getProperty("USE_LOCAL_MOCKS")

        if (useLocalMocks.toBoolean()) {
            get() // Fallback lookup handles local standalone sandbox loops
        } else {
            RemoteAuth(
                client = get<SupabaseClient>(),
                scope = get<CoroutineScope>()
            )
        }
    }
}
