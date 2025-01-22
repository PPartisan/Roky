package chatroom

import chatroom.sendmessages.SendMessagePanel
import chatroom.sendmessages.sendMessagesModule
import chatroom.users.UsersPanel
import chatroom.users.usersModule
import chatroom.viewmessages.ViewMessagesPanel
import chatroom.viewmessages.viewMessagesModule
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module

val chatroomModules =
    module {
        includes(sendMessagesModule, usersModule, viewMessagesModule)
        scope<ChatroomWindow> {
            scopedOf(::UsersPanel)
            scopedOf(::SendMessagePanel)
            scopedOf(::ViewMessagesPanel)
        }
    }
