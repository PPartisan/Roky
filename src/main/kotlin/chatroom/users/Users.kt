package chatroom.users

import chatroom.ChatroomWindow
import org.koin.dsl.module

val usersModule =
    module {
        scope<ChatroomWindow> {
            scoped { UsersPanel(presenter = get()) }
            scoped { UsersListUseCase() }
            scoped { UsersListPresenter(users = get(), scope = get<ChatroomWindow>().windowScope, dispatchers = get()) }
        }
    }
