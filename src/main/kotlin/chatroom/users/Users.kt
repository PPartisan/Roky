package chatroom.users

import chatroom.ChatroomWindow
import chatroom.LEFT_WIDTH
import org.koin.dsl.module

val usersModule =
    module {
        scope<ChatroomWindow> {
            scoped { UsersPanel(presenter = get()) }
            scoped { UsersListUseCase() }
            scoped {
                UsersListPresenter(
                    truncate = get(),
                    repository = get(),
                    scope = get<ChatroomWindow>().windowScope,
                    dispatchers = get(),
                )
            }
        }
        factory { UsersListTruncation(LEFT_WIDTH - 3) }
    }
