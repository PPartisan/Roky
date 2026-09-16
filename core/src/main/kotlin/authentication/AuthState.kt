package authentication

sealed interface AuthState {
    data class SignIn(val user: String) : AuthState

    data object InvalidCredentials : AuthState

    data object Authenticating : AuthState

    data object InitState : AuthState
}
