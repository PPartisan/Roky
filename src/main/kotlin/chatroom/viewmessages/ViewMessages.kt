package chatroom.viewmessages

import chatroom.ChatroomWindow
import chatroom.VIEW_MESSAGES_WIDTH
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
                    smartWrap = SmartWrap(VIEW_MESSAGES_WIDTH).also { println("ScreenWidth = $VIEW_MESSAGES_WIDTH") }::invoke,
                )
            }
            scoped { DisplayableMessages(get()) }
        }
    }
