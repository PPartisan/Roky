package profile

import chatserver.LoggedInUserId
import chatserver.ProfileResult
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import chatserver.profiles.SupabaseProfilesRepository.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import profile.ValidateUsernameRequest.Result.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ValidateUsernameRequest(
    private val write: WriteChatRepository<String>,
    private val read: ReadChatRepository<ProfileResult>,
    private val userId: LoggedInUserId,
) {
    suspend operator fun invoke(username: String): Result {
        if (username.isBlank()) {
            return UsernameBlank
        }

        val loggedInUser = userId()
        if (loggedInUser.isBlank()) {
            return LoggedInUsernameBlank
        }

        val profiles = read.latest()
        val me = profiles.item[loggedInUser]
        if (me?.username == username) {
            return UsernameUnchanged
        }

        return read.observe().waitForValue(Profile(loggedInUser, username))
    }

    private suspend fun Flow<ProfileResult>.waitForValue(
        target: Profile,
        timeout: Duration = 4.seconds,
    ): Result {
        write.write(target.username)
        val result =
            withTimeoutOrNull(timeout) {
                first { it.isOk && it.item[target.id] == target }
            }
        return if (result != null) Ok else Timeout
    }

    sealed interface Result {
        val message: String

        data object Ok : Result {
            override val message = ""
        }

        data object UsernameBlank : Result {
            override val message: String = "Requested username must not be blank."
        }

        data object LoggedInUsernameBlank : Result {
            override val message = "User ID must not be blank."
        }

        data object UsernameUnchanged : Result {
            override val message: String = "Current username must not match requested username."
        }

        data object Timeout : Result {
            override val message: String = "Server timeout! Trying to change username"
        }
    }
}
