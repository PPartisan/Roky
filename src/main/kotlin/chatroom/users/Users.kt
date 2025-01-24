package chatroom.users

import chatroom.ChatroomWindow
import org.koin.dsl.module

val usersModule =
    module {
        scope<ChatroomWindow> {
            scoped { UsersPanel() }
        }
    }
