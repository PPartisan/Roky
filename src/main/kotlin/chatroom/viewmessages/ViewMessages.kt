package chatroom.viewmessages

import chatroom.ChatroomWindow
import chatserver.ChatRepositories
import org.koin.dsl.module
import utils.SmartWrapIndenting

val viewMessagesModule =
    module {
        scope<ChatroomWindow> {
            scoped { ViewMessagesPanel(get()) }
            scoped {
                ViewMessagesPresenter(
                    windowScope = get<ChatroomWindow>().windowScope,
                    dispatchers = get(),
                    read = get(),
                    channel = get<ChatRepositories>().subscribeMessages(),
                )
            }
            scoped { DisplayableMessages(get(), get()) }
        }
        factory { SmartWrapIndenting() }
    }
