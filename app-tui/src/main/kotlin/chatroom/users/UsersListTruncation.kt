package chatroom.users

import utils.cutOff

class UsersListTruncation(private val width: Int) {
    operator fun invoke(input: String): String = input.cutOff(width)
}
