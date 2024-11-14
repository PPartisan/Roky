package login

import login.LoginEvent.Login
import login.LoginViewState.Idle

class LoginUseCase {
    operator fun invoke(event: Login): LoginViewState = with(event){
        val status = when {
            username.isBlank() -> ERROR_USERNAME
            password.isBlank() -> ERROR_PASSWORD
            else -> LOGIN_SUCCESS
        }
        return Idle(userName = event.username, password = "", status = status)
    }

    companion object {
        const val ERROR_USERNAME = "Username cannot be blank."
        const val ERROR_PASSWORD = "Password cannot be blank."
        const val LOGIN_SUCCESS = "Username and password is OK."
    }
}