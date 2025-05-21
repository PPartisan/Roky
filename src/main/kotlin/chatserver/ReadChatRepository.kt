package chatserver

import kotlinx.coroutines.flow.Flow

interface ReadChatRepository<T : ReadChatRepository.ReadResult<*>> {
    fun latest(): T

    fun observe(): Flow<T>

    sealed interface ReadResult<T> {
        val isOk: Boolean
        val item: T
        val error: Exception?
    }
}
