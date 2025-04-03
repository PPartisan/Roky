package authentication

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RemoteAuth(
    private val client: SupabaseClient,
    private val scope: CoroutineScope,
) : Auth {
    private val _state: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.InitState)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        scope.launch {
            client.auth.sessionStatus.collect {
                _state.value = it.toRokyState()
                println("Current State: ${state.value}")
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
        println("Remote Auth logout")
    }

    override suspend fun isLoggedIn(): Boolean {
        println("Remote Auth is logged in")
        return state.value is AuthState.SignIn
    }

    companion object {
        fun SessionStatus.toRokyState(): AuthState =
            when (this) {
                is SessionStatus.Authenticated -> AuthState.SignIn(session.user?.id ?: "Unknown User")
                is SessionStatus.NotAuthenticated -> AuthState.InvalidCredentials
                else -> {
                    println("Unrecognised State: $this")
                    AuthState.InvalidCredentials
                }
            }
    }
}

sealed interface AuthState {
    data class SignIn(val user: String) : AuthState

    data object InvalidCredentials : AuthState

    data object Authenticating : AuthState

    data object InitState : AuthState
}
