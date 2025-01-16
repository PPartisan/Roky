package login

import authentication.Authenticator
import login.LoginEvent.Login
import login.LoginViewState.Idle

class LoginUseCase(
    private val authenticator: Authenticator
) {
    suspend operator fun invoke(event: Login): LoginViewState = with(event) {
        val status = when {
            username.isBlank() -> ERROR_USERNAME
            password.isBlank() -> ERROR_PASSWORD
            else -> if (authenticator.login(username, password)) LOGIN_SUCCESS else LOGIN_FAILURE
        }
        return Idle(userName = event.username, password = "", status = status)
    }

    companion object {
        const val ERROR_USERNAME = "Username cannot be blank."
        const val ERROR_PASSWORD = "Password cannot be blank."
        const val LOGIN_SUCCESS = "Username and password is OK."
        const val LOGIN_FAILURE = "Could not authenticate."
    }
}
