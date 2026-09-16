package authentication

import kotlinx.coroutines.flow.Flow

interface ReadAuth {
    fun state(): Flow<AuthState>

    fun getState(): AuthState
}
