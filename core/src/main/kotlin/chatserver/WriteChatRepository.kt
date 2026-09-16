package chatserver

fun interface WriteChatRepository<in T> {
    fun write(item: T)
}
