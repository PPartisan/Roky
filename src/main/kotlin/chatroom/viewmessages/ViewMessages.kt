package chatroom.viewmessages

import chatroom.ChatroomWindow
import org.koin.dsl.module

val viewMessagesModule =
    module {
        scope<ChatroomWindow> {
            scoped { ViewMessagesPanel(get()) }
            scoped {
                ViewMessagesPresenter(
                    windowScope = get<ChatroomWindow>().windowScope,
                    dispatchers = get(),
                )
            }
        }
    }
