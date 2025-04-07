package authentication

import Secrets
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope

interface Auth {
    suspend fun login(
        email: String,
        password: String,
    )

    suspend fun logout()

    suspend fun isLoggedIn(): Boolean

    class Factory(
        private val client: SupabaseClient,
        private val coroutineScope: CoroutineScope,
    ) {
        private val remote by lazy { RemoteAuth(client, coroutineScope) }

        fun create(): Auth = if (Secrets.USE_LOCAL_MOCKS) LocalAuth else remote

        fun reader(): ReadAuth = if (Secrets.USE_LOCAL_MOCKS) LocalAuth else remote
    }
}
