package chatroom.users

import chatserver.ChatRepositories
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.jetbrains.annotations.VisibleForTesting

class DisplayableUsersList(
    private val repositories: ChatRepositories,
) {
    operator fun invoke(): Flow<List<String>> {
        // RESOLUTION: Streams the raw generic Set<String> matching our pure domain read contracts
        val profileIds: Flow<Set<String>> =
            repositories.readPresence().observe()

        // RESOLUTION: Process usernames as a pure domain Map<String, String> to drop framework leakage
        val usernames: Flow<Map<String, String>> =
            repositories.readProfiles().observe()

        return combine(usernames, profileIds) { uName, pId ->
            pId.map { uName[it] ?: UNKNOWN_USER }
        }
    }

    companion object {
        @VisibleForTesting
        const val UNKNOWN_USER = "Unknown"
    }
}
