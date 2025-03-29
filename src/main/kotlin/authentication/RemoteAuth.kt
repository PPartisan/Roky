package authentication

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

// SUPABASEEEE
object RemoteAuth : Auth {
    private var isLoggedIn: Boolean = false

    override suspend fun login(
        email: String,
        password: String,
    ) {
        println("Remote Auth login")
        delay(3.seconds)
        isLoggedIn = (email in validUsers && password == PASSWORD)
    }

    override suspend fun logout() {
        println("Remote Auth logout")
        isLoggedIn = false
    }

    override suspend fun isLoggedIn(): Boolean {
        println("Remote Auth is logged in")
        return isLoggedIn
    }

    private val validUsers =
        listOf("Robert", "Dunia", "Tom", "Max", "Casper", "Ed", "Kai", "Laura", "Niamh", "Sofia")
    private const val PASSWORD = "ILoveRoky"
}
