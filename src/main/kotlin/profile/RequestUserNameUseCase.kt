package profile

import kotlinx.coroutines.delay
import profile.ProfileViewState.Failed
import profile.ProfileViewState.Success
import kotlin.time.Duration.Companion.seconds

class RequestUserNameUseCase(
    private val request: RequestUserName,
) {
    suspend operator fun invoke(username: String): ProfileViewState {
        if (username.isBlank()) {
            return Failed(ERROR_USERNAME_BLANK)
        }
        return with(username) {
            if (request(this)) toSuccess() else toFailed()
        }
    }

    companion object {
        const val ERROR_USERNAME_BLANK = "Username cannot be blank."

        private fun String.toSuccess(): ProfileViewState = Success("Changed username to $this")

        private fun String.toFailed(): ProfileViewState = Failed("Could not change username to $this")
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
