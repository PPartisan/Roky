package chatroom.viewmessages

import chatserver.ChatRepositories
import kotlinx.coroutines.flow.*

class DisplayableMessages(private val repositories: ChatRepositories) {
    operator fun invoke(): Flow<String> {
        val messages =
            repositories.readMessages().observe()
                .filter { it.isOk }
                .map { it.item.lastOrNull() }
                .filterNotNull()
        val username =
            repositories.readProfiles().observe()
                .filter { it.isOk }
                .map { it.item }
        return combine(messages, username) { message, usernames ->
            val user = usernames[message.userId]?.username ?: "anon"
            "$user: ${message.message}"
        }
    }
}
