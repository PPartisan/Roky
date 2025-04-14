package chatserver

import chatserver.ReadChatRepository.ReadResult

data class ProfileResult(
    override val isOk: Boolean,
    override val item: Map<String, String>,
    override val error: Exception?,
) :
    ReadResult<Map<String, String>> {
    companion object {
        fun ok(item: Map<String, String>): ProfileResult = ProfileResult(true, item, null)

        fun fail(e: Exception? = null): ProfileResult = ProfileResult(false, emptyMap(), e)
    }
}
