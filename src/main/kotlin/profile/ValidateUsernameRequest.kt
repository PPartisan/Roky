package profile

import chatserver.LoggedInUserId
import chatserver.ProfileResult
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import profile.ValidateUsernameRequest.Result.*

class ValidateUsernameRequest(
    private val write: WriteChatRepository<String>,
    private val read: ReadChatRepository<ProfileResult>,
    private val userId: LoggedInUserId,
) {

    suspend operator fun invoke(username: String): Result {
        if (username.isBlank())
            return UsernameBlank

        val loggedInUser = userId()
        if (loggedInUser.isBlank())
            return LoggedInUsernameBlank

        val profiles = read.latest()
        if (profiles.item[loggedInUser]?.username == username)
            return UsernameUnchanged

        write.write(username)
        return with(read.latest()) {
            if (isOk) Ok else ServerError(error!!)
        }
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

        data class ServerError(val error: Exception) : Result {
            override val message: String = error.localizedMessage
        }
    }
}
