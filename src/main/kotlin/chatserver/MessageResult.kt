package chatserver

import chatserver.ReadChatRepository.ReadResult

data class MessageResult(
    override val isOk: Boolean,
    override val item: List<String>,
    override val error: Exception?,
) :
    ReadResult<List<String>> {
    companion object {
        fun ok(item: List<String>): MessageResult = MessageResult(true, item, null)

        fun fail(e: Exception? = null): MessageResult = MessageResult(false, emptyList(), e)
    }
}
