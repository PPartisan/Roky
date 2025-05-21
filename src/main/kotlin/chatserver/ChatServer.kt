package chatserver

import chatserver.MessagesRepository.Read
import chatserver.MessagesRepository.Write
import chatserver.profiles.chatServerProfilesModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.binds
import org.koin.dsl.module

val chatServerModule =
    module {
        includes(chatServerProfilesModule)
        factory { MockMessages } binds arrayOf(Read::class, Write::class)
        factoryOf(::ChatRepositories)
    }
