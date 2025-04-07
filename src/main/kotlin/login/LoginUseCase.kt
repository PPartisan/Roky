package login

import authentication.Auth
import login.LoginEvent.Login

class LoginUseCase(
    private val authenticator: Auth,
) {
    suspend operator fun invoke(event: Login): LoginResult =
        with(event) {
            when {
                username.isBlank() -> LoginResult.fail(ERROR_USERNAME)
                password.isBlank() -> LoginResult.fail(ERROR_PASSWORD)
                else -> {
                    authenticator.login(username, password)
                    LoginResult.ok()
                }
            }
        }

    data class LoginResult(
        val isSuccessful: Boolean,
        val message: String?,
    ) {
        companion object {
            fun ok(): LoginResult = LoginResult(true, null)

            fun fail(message: String): LoginResult = LoginResult(false, message)
        }
    }

    companion object {
        const val ERROR_USERNAME = "Username cannot be blank."
        const val ERROR_PASSWORD = "Password cannot be blank."
    }
}
