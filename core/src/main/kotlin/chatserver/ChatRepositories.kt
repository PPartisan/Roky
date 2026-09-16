package chatserver

import chatserver.messages.Message // Imports our pure domain message data class cleanly

// Declares a package-level visibility placeholder for configuration toggles safely.
// This allows presenters to route logic seamlessly while keeping :core isolated from the infrastructure.
private const val USE_LOCAL_MOCKS = true

/**
 * Unified Chat Repository Compound Architectural Contract
 *
 * Provides a type-safe, compile-time verified abstraction that combines reading, writing,
 * and streaming behaviors. This guarantees that swapping between local mock systems
 * and live cloud platforms will never cause runtime unchecked casting failures.
 */
interface UnifiedChatRepository<T> : ReadChatRepository<T>, WriteChatRepository<String>, SubscribeChatRepository

/**
 * ChatRepositories Abstraction Hub
 *
 * JUSTIFICATION FOR THE TEAM:
 * Decouples core business workflows from concrete storage layers. Presenters and managers
 * hit this hub blindly, allowing the engine to transparently toggle backend environments
 * via structural feature configurations without triggering ClassCastException bugs.
 */
class ChatRepositories(
    private val localProfiles: UnifiedChatRepository<Map<String, String>>,
    private val localMessages: UnifiedChatRepository<List<Message>>,
    private val localPresence: UnifiedChatRepository<Set<String>>,
    private val remoteProfiles: UnifiedChatRepository<Map<String, String>>,
    private val remoteMessages: UnifiedChatRepository<List<Message>>,
    private val remotePresence: UnifiedChatRepository<Set<String>>
) {
    // --- Profile Management Pipelines ---
    fun writeProfiles(): WriteChatRepository<String> = if (USE_LOCAL_MOCKS) localProfiles else remoteProfiles
    fun readProfiles(): ReadChatRepository<Map<String, String>> = if (USE_LOCAL_MOCKS) localProfiles else remoteProfiles
    fun subscribeProfiles(): SubscribeChatRepository = if (USE_LOCAL_MOCKS) localProfiles else remoteProfiles

    // --- Core Messaging Pipelines ---
    fun readMessages(): ReadChatRepository<List<Message>> = if (USE_LOCAL_MOCKS) localMessages else remoteMessages
    fun writeMessages(): WriteChatRepository<String> = if (USE_LOCAL_MOCKS) localMessages else remoteMessages
    fun subscribeMessages(): SubscribeChatRepository = if (USE_LOCAL_MOCKS) localMessages else remoteMessages

    // --- Real-time User Presence Pipelines ---
    fun readPresence(): ReadChatRepository<Set<String>> = if (USE_LOCAL_MOCKS) localPresence else remotePresence
    fun subscribePresence(): SubscribeChatRepository = if (USE_LOCAL_MOCKS) localPresence else remotePresence
}
