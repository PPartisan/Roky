package chatserver.supabase

import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import chatserver.WriteChatRepository
import chatserver.messages.Message
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

/**
 * Supabase Platform Infrastructure Integration Dependency Module
 *
 * JUSTIFICATION FOR THE TEAM:
 * This serves as the centralized orchestration block for our data adapter layer.
 * Decouples the infrastructure layer entirely from generated UI classes by reading
 * configuration keys directly out of the Koin Property Parameter context maps.
 */
val infrastructureSupabaseModule = module {

    // Core Supabase API Client Singleton initialization
    single {
        // Dynamically extracts properties fed from the Composition Root at runtime
        val serverUrl: String = getProperty("SUPABASE_URL")
        val clientKey: String = getProperty("SUPABASE_KEY")

        createSupabaseClient(
            supabaseUrl = serverUrl,
            supabaseKey = clientKey
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }

    // Managed background context pool ensuring remote database routines execute safely in isolation
    single {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    // --- Concrete Repository Instance Allocations ---
    single { SupabaseProfilesRepository(get(), get()) }
    single { SupabaseMessageRepository(get(), get()) }
    single { SupabasePresenceRepository(get(), get()) }

    // --- Granular Presentation Port Interface Bindings ---
    // Maps your repositories directly to their exact base contracts to pass dependency resolution sweeps
    single<ReadChatRepository<Map<String, String>>> { get<SupabaseProfilesRepository>() }
    single<WriteChatRepository<String>> { get<SupabaseProfilesRepository>() }
    single<SubscribeChatRepository> { get<SupabaseProfilesRepository>() }

    single<ReadChatRepository<List<Message>>> { get<SupabaseMessageRepository>() }
    single<SubscribeChatRepository> { get<SupabaseMessageRepository>() }

    single<ReadChatRepository<Set<String>>> { get<SupabasePresenceRepository>() }
    single<SubscribeChatRepository> { get<SupabasePresenceRepository>() }
}
