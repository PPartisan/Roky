package chatroom.viewmessages

import chatserver.ChatRepositories
import kotlinx.coroutines.flow.*
import utils.SmartWrapIndenting

class DisplayableMessages(
    private val repositories: ChatRepositories,
    private val wrap: SmartWrapIndenting,
) {
    operator fun invoke(): Flow<String> =
        repositories.readMessages().observe()
            .filter { it.isOk }
            .map { it.item.lastOrNull() }
            .filterNotNull()
            .map { message ->
                val usernames = repositories.readProfiles().latest().takeIf { it.isOk }?.item ?: emptyMap()
                val user = usernames[message.userId]?.username ?: "anon"
                "$user: ${message.message}"
            }
            .wrapText()

    private fun Flow<String>.wrapText() = map(wrap::invoke)
}
