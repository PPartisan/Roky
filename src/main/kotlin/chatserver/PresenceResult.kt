package chatserver

import chatserver.ReadChatRepository.ReadResult

data class PresenceResult(
    override val isOk: Boolean,
    override val item: Map<String, Boolean>,
    override val error: Exception?,
) :
    ReadResult<Map<String, Boolean>> {
    companion object {
        fun ok(item: Map<String, Boolean>): PresenceResult = PresenceResult(true, item, null)

        fun fail(e: Exception? = null): PresenceResult = PresenceResult(false, emptyMap(), e)
    }
}
