package chatroom.viewmessages

import chatroom.ChatroomWindow
import chatroom.SEND_MESSAGES_WIDTH
import chatserver.ChatRepositories
import org.koin.dsl.module
import utils.SmartWrap

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
            scoped { SmartWrap(SEND_MESSAGES_WIDTH) }
        }
    }
