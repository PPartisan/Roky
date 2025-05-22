package chatserver.messages

import org.koin.dsl.module

val chatServerMessagesModule =
    module {
        single { LocalChatMessages(get()) }
    }
