package authentication

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class Authenticator {

    private var isLoggedIn : Boolean = true

    suspend fun login(username: String, password: String) : Boolean {
        delay(3.seconds.inWholeMilliseconds)
        return (username in validUsers && password == PASSWORD).also { isLoggedIn = it }
    }

    suspend fun isLoggedIn(): Boolean{
        delay(1.seconds.inWholeMilliseconds)
        return isLoggedIn
    }

    companion object {
        private val validUsers = listOf("Tom", "Dunia", "Max")
        private const val PASSWORD = "ILoveRoky"
    }

}
