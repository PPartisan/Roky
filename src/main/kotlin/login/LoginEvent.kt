package login

sealed interface LoginEvent {
    data class Login (
        val username: String,
        val password: String
    ) : LoginEvent
}
