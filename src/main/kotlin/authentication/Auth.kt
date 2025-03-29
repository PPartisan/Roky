package authentication

import Secrets

interface Auth {
    suspend fun login(
        email: String,
        password: String,
    )

    suspend fun logout()

    suspend fun isLoggedIn(): Boolean

    class Factory {
        fun create(): Auth = if (Secrets.USE_LOCAL_MOCKS) LocalAuth else RemoteAuth
    }
}
