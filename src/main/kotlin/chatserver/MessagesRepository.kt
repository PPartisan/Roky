package chatserver

import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    interface Read {
        fun observe(): Flow<String>
    }

    interface Write {
        suspend fun send(message: String)
    }
}
