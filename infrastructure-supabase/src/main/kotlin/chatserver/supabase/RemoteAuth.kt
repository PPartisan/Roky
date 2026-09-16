package chatserver.supabase

import authentication.Auth
import authentication.AuthState
import authentication.ReadAuth
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.SessionStatus // Targets the correct top-level enum path directly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Supabase Remote Authentication Infrastructure Adapter
 *
 * JUSTIFICATION FOR THE TEAM:
 * Implements the domain 'Auth' and 'ReadAuth' interface ports. Encapsulates concrete cloud
 * singletons and Ktor driver engines safely inside the infrastructure module layer, keeping
 * our core business rules completely blind to provider SDKs.
 */
class RemoteAuth(
    private val client: SupabaseClient,
    private val scope: CoroutineScope,
) : Auth, ReadAuth {
    private val _state: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.InitState)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        scope.launch {
            // RESOLUTION: Aligned with Supabase 2.1.0 top-level sessionStatus flow APIs natively,
            // mapping Authenticated tokens cleanly into our core domain data states.
            client.auth.sessionStatus.collect { status ->
                _state.value = when (status) {
                    is SessionStatus.Authenticated -> {
                        val userId = status.session.user?.id ?: "Unknown User"
                        AuthState.SignIn(userId)
                    }
                    is SessionStatus.NotAuthenticated -> AuthState.InvalidCredentials
                    else -> AuthState.InitState
                }
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String,
    ) {
        _state.value = AuthState.Authenticating
        try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        } catch (e: Exception) {
            _state.value = AuthState.InvalidCredentials
        }
    }

    override suspend fun logout() {
        try {
            client.auth.signOut()
        } catch (e: Exception) {
            // Fallback grace boundary catch block
        }
    }

    override suspend fun isLoggedIn(): Boolean = state.value is AuthState.SignIn

    override fun state(): Flow<AuthState> = state

    override fun getState(): AuthState = state.value
}
