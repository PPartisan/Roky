package authentication

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

object LocalAuth : Auth {
    private var isLoggedIn: Boolean = false

    override suspend fun login(
        email: String,
        password: String,
    ) {
        delay(3.seconds)
        println("local Auth login")
        isLoggedIn = (email in validUsers && password == PASSWORD)
    }

    override suspend fun logout() {
        println("local Auth logout")
        isLoggedIn = false
    }

    override suspend fun isLoggedIn(): Boolean {
        println("local local is logged in")
        return isLoggedIn
    }

    private val validUsers =
        listOf("Robert", "Dunia", "Tom", "Max", "Casper", "Ed", "Kai", "Laura", "Niamh", "Sofia")
    private const val PASSWORD = "ILoveRoky"
}
