package chatserver

import Secrets.USE_LOCAL_MOCKS
import chatserver.messages.LocalChatMessages
import chatserver.messages.SupabaseMessageRepository
import chatserver.presence.LocalPresenceRepository
import chatserver.presence.SupabasePresenceRepository
import chatserver.profiles.LocalProfilesRepository
import chatserver.profiles.SupabaseProfilesRepository

class ChatRepositories(
    private val localProfiles: LocalProfilesRepository,
    private val localMessages: LocalChatMessages,
    private val localPresence: LocalPresenceRepository,
    private val remoteProfiles: SupabaseProfilesRepository,
    private val remoteMessages: SupabaseMessageRepository,
    private val remotePresence: SupabasePresenceRepository
) {
    fun writeProfiles(): WriteChatRepository<String> =
        if (USE_LOCAL_MOCKS) localProfiles else remoteProfiles

    fun readProfiles(): ReadChatRepository<ProfileResult> =
        if (USE_LOCAL_MOCKS) localProfiles else remoteProfiles

    fun subscribeProfiles(): SubscribeChatRepository =
        if (USE_LOCAL_MOCKS) localProfiles else remoteProfiles

    fun readMessages(): ReadChatRepository<ChatMessageResult> =
        if (USE_LOCAL_MOCKS) localMessages else remoteMessages

    fun subscribeMessages(): SubscribeChatRepository = localMessages

    fun writeMessages(): WriteChatRepository<String> = localMessages

    fun subscribePresence(): SubscribeChatRepository = localPresence

    fun readPresence(): ReadChatRepository<PresenceResult> = localPresence
}
