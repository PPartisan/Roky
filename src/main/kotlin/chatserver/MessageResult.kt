package chatserver

import chatserver.ReadChatRepository.ReadResult

data class MessageResult(
    override val isOk: Boolean,
    override val item: List<Message>,
    override val error: Exception?,
) :
    ReadResult<List<Message>> {
    companion object {
        fun ok(item: List<Message>): MessageResult = MessageResult(true, item, null)

        fun fail(e: Exception? = null): MessageResult = MessageResult(false, emptyList(), e)
    }
}

data class Message(
    val userId: String,
    val message: String,
)
