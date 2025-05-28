package chatroom.sendmessages

import chatroom.ChatroomWindow
import org.koin.dsl.module

val sendMessagesModule =
    module {
        scope<ChatroomWindow> {
            scoped {
                SendMessagePresenter(
                    dispatchers = get(),
                    windowScope = get<ChatroomWindow>().windowScope,
                )
            }
            scoped { SendMessagePanel(get()) }
        }
    }
