package authentication

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration.Companion.seconds

object LocalAuth : Auth, ReadAuth {
    private val _state: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.InitState)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    override suspend fun login(
        email: String,
        password: String,
    ) {
        delay(3.seconds)
        val state =
            if (email in validUsers && password == PASSWORD) {
                AuthState.SignIn(
                    email,
                )
            } else {
                AuthState.InvalidCredentials
            }
        this._state.value = state
    }

    override suspend fun logout() {
        _state.value = AuthState.InitState
    }

    override suspend fun isLoggedIn(): Boolean = _state.value is AuthState.SignIn

    private val validUsers =
        listOf("Robert", "Dunia", "Tom", "Max", "Casper", "Ed", "Kai", "Laura", "Niamh", "Sofia")
    private const val PASSWORD = "1"

    override fun state(): Flow<AuthState> = state

    override fun getState(): AuthState = state.value
}
