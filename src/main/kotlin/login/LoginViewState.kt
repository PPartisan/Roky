package login

sealed interface LoginViewState {
    val userName: String
    val password: String
    val status: String

    data class Idle(
        override val userName: String = "",
        override val password: String = "",
        override val status: String = ""
    ) : LoginViewState

    data class Authenticating(
        override val userName: String = "",
        override val password: String = "",
        override val status: String = ""
    ) : LoginViewState
}
