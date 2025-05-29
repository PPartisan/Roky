package chatserver

import chatserver.messages.chatServerMessagesModule
import chatserver.profiles.chatServerProfilesModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val chatServerModule =
    module {
        includes(chatServerProfilesModule, chatServerMessagesModule)
        factoryOf(::ChatRepositories)
    }
