package chatroom.viewmessages

import chatserver.ChatRepositories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import utils.SmartWrap
import java.lang.System.lineSeparator

class DisplayableMessages(
    private val repositories: ChatRepositories,
    private val wrap: SmartWrap,
) {
    operator fun invoke(): Flow<List<String>> =
        repositories.readMessages().observe()
            .filter { it.isOk }
            .mapNotNull { it.item.lastOrNull() }
            .map { message ->
                val usernames = repositories.readProfiles().latest().takeIf { it.isOk }?.item ?: emptyMap()
                val user = usernames[message.userId]?.username ?: "anon"
                "$user: ${message.message}"
                    .removeLineBreaks()
                    .wrap()
                    .toList()
            }

    private fun String.wrap(): String = wrap(this)

    companion object {
        private fun String.removeLineBreaks(): String = replace("[\r\n]+".toRegex(), " ").trim()

        private fun String.toList(): List<String> = split(lineSeparator())
    }
}
