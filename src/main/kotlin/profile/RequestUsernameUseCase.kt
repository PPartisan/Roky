package profile

import chatserver.WriteChatRepository

class RequestUsernameUseCase(
    private val profiles: WriteChatRepository<String>
) {
    operator fun invoke(username: String) {
        profiles.write(username)
    }
}

