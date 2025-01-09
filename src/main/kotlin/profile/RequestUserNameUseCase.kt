package profile

import kotlinx.coroutines.delay
import profile.ProfileViewState.*
import kotlin.time.Duration.Companion.seconds

class RequestUserNameUseCase(
    private val request:RequestUserName
) {
    suspend operator fun invoke(username: String): ProfileViewState {
        if (username.isBlank()) {
            return Failed(ERROR_USERNAME_BLANK)
        }
        val isSuccess = request(username)
        return if (isSuccess) {
            Success(username.successMessage())
        } else {
            Failed(username.failureMessage())
        }
    }

    companion object {
        const val ERROR_USERNAME_BLANK = "Username cannot be blank."
        private fun String.successMessage() =
            "Changed username to $this"
        private fun String.failureMessage() =
            "Could not change username to $this"
    }

    class RequestUserName {
        suspend operator fun invoke(username: String): Boolean {
            delay(2.seconds)
            return username.length < VALID_LENGTH
        }
        companion object {
            private const val VALID_LENGTH = 6
        }
    }
}
