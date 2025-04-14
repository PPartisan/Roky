package chatserver

interface WriteChatRepository<T> {
    fun write(item: T)
}
