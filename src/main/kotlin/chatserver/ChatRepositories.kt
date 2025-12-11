package chatserver

import chatserver.messages.LocalChatMessages
import chatserver.presence.LocalPresenceRepository
import chatserver.profiles.LocalProfilesRepository

class ChatRepositories(
    private val profiles: LocalProfilesRepository,
    private val messages: LocalChatMessages,
    private val presence: LocalPresenceRepository,
) {
    fun writeProfiles(): WriteChatRepository<String> = profiles

    fun readProfiles(): ReadChatRepository<ProfileResult> = profiles

    fun subscribeProfiles(): SubscribeChatRepository = profiles

    fun readMessages(): ReadChatRepository<ChatMessageResult> = messages

    fun subscribeMessages(): SubscribeChatRepository = messages

    fun writeMessages(): WriteChatRepository<String> = messages

    fun subscribePresence(): SubscribeChatRepository = presence

    fun readPresence(): ReadChatRepository<PresenceResult> = presence
}
