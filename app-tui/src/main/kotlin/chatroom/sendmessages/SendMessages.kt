package chatroom.sendmessages

import chatroom.ChatroomWindow
import chatserver.ChatRepositories
import org.koin.dsl.module

val sendMessagesModule =
    module {
        scope<ChatroomWindow> {
            scoped {
                SendMessagePresenter(
                    dispatchers = get(),
                    message = get<ChatRepositories>().writeMessages(),
                    windowScope = get<ChatroomWindow>().windowScope,
                )
            }
            scoped { SendMessagePanel(get()) }
        }
    }
