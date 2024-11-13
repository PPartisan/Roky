package login

import login.LoginEvent.Login
import login.LoginViewState.Idle

class LoginUseCase {
    operator fun invoke (event: Login) : LoginViewState {
        if (event.username.isBlank()) {
            return Idle(status="Username cannot be blank.")
        }
        else if (event.password.isBlank()) {
            return Idle(status="Password cannot be blank.")
        }
        return Idle(userName = event.username, password = event.password, status = "Username and password is OK.")
    }
}