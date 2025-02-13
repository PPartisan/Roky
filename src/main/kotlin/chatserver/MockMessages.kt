package chatserver

import chatserver.MessagesRepository.Read
import chatserver.MessagesRepository.Write
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object MockMessages : Read, Write {
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 64)
    private val events = _events.asSharedFlow()

    override fun observe(): Flow<String> {
        return events
    }

    override suspend fun send(message: String) {
        _events.emit(message)
    }
}
