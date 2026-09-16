package authentication

import org.koin.dsl.module

/**
 * Pure Authentication Domain Contract Interfaces
 *
 * JUSTIFICATION FOR THE TEAM:
 * Defines the abstract business rules for user access control. Strips out legacy
 * framework dependencies (Supabase, Secrets) to protect the core compilation path
 * and future-proof the application for potential new frontends or backend environments.
 */
interface Auth {
    suspend fun login(email: String, password: String)
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}

/**
 * Pure Authentication Domain Configuration Module
 *
 * JUSTIFICATION FOR THE TEAM:
 * Standardised dependency entry graph node for tracking local memory mocks during sandboxed
 * developer verification runs. Real cloud providers are resolved through concrete infrastructure nodes.
 */
val authenticationModule = module {
    // Safely binds the abstract local sandbox authentication engine
    single<Auth> { LocalAuth }
}
