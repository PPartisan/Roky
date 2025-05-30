package chatserver

import chatserver.ReadChatRepository.ReadResult

data class ChatMessageResult(
    override val isOk: Boolean,
    override val item: String,
    override val error: Exception?,
) :
    ReadResult<String> {
    companion object {
        fun ok(item: String): ChatMessageResult = ChatMessageResult(true, item, null)

        fun fail(e: Exception? = null): ChatMessageResult = ChatMessageResult(false, "", e)
    }
}
