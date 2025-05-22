package chatserver

import chatserver.messages.LocalChatMessages
import chatserver.profiles.LocalProfilesRepository

class ChatRepositories(
    private val profiles: LocalProfilesRepository,
    private val messages: LocalChatMessages,
) {
    fun writeProfiles(): WriteChatRepository<String> = profiles

    fun readProfiles(): ReadChatRepository<ProfileResult> = profiles

    fun subscribeProfiles(): SubscribeChatRepository = profiles

    fun readMessages(): ReadChatRepository<ChatMessageResult> = messages
}
