package chatroom.users

import chatserver.ChatRepositories
import chatserver.profiles.SupabaseProfilesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.jetbrains.annotations.VisibleForTesting

class DisplayableUsersList (
    private val repositories: ChatRepositories
) {
    operator fun invoke () : Flow<List<String>> {
        val profileIds : Flow<Set<String>> =
            repositories.readPresence().observe().map { it.item }
        val usernames : Flow<Map<String, SupabaseProfilesRepository.Profile>> =
            repositories.readProfiles().observe().map { it.item }
        return combine (usernames, profileIds) { uName , pId ->
            pId.map { uName[it]?.username?.blankToNull() ?: UNKNOWN_USER}
        }
    }
    companion object{
        @VisibleForTesting
        const val UNKNOWN_USER = "Unknown"


        private fun String.blankToNull(): String=
            ifBlank { UNKNOWN_USER }
    }
}

