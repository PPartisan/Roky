package chatserver

import chatserver.profiles.LocalProfilesRepository

class ChatRepositories(
    private val profiles: LocalProfilesRepository,
) {
    fun writeProfiles(): WriteChatRepository<String> = profiles

    fun readProfiles(): ReadChatRepository<ProfileResult> = profiles

    fun subscribeProfiles(): SubscribeChatRepository = profiles
}
