package chatroom.viewmessages

import chatroom.ChatroomWindow
import chatserver.MessageCoordinator // RESOLUTION: Swapped generic legacy repository interfaces with pure central coordinator reference
import org.koin.dsl.module

val viewMessagesModule =
    module {
        scope<ChatroomWindow> {
            scoped { ViewMessagesPanel(get()) }
            scoped {
                ViewMessagesPresenter(
                    windowScope = get<ChatroomWindow>().windowScope,
                    dispatchers = get(),
                    coordinator = get<MessageCoordinator>() // RESOLUTION: Injects the central multicast MessageCoordinator singleton matching constructor refactors
                )
            }
        }
    }
