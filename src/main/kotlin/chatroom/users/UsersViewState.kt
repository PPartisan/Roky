package chatroom.users

interface UsersViewState {
    data object Empty : UsersViewState

    data class Users(val users: List<String>) : UsersViewState
}
