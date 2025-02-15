package chatserver

import chatserver.MessagesRepository.Read
import chatserver.MessagesRepository.Write
import org.koin.dsl.binds
import org.koin.dsl.module

val chatServerModule =
    module {
        factory { MockMessages } binds arrayOf(Read::class, Write::class)
    }
