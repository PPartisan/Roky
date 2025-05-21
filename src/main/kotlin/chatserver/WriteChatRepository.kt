package chatserver

fun interface WriteChatRepository<T> {
    fun write(item: T)
}
