package chatserver

import chatserver.ReadChatRepository.ReadResult

data class PresenceResult(
    override val isOk: Boolean,
    override val item: Set<String>,
    override val error: Exception?,
) :
    ReadResult<Set<String>> {
    companion object {
        fun ok(item: Set<String>): PresenceResult = PresenceResult(true, item, null)

        fun fail(e: Exception? = null): PresenceResult = PresenceResult(false, emptySet(), e)
    }
}
