package login

import io.kotest.matchers.equals.shouldBeEqual
import login.LoginEvent.*
import login.LoginUseCase.Companion.ERROR_PASSWORD
import login.LoginUseCase.Companion.ERROR_USERNAME
import login.LoginUseCase.Companion.LOGIN_SUCCESS
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoginUseCaseTest {
    private lateinit var logIn: LoginUseCase

    @BeforeEach
    fun setUp() {
        logIn = LoginUseCase()
    }

    @Test
    fun `when username is blank, then status is username cannot be blank`() {
        val event = Login(username = "", password = "")
        logIn(event).status shouldBeEqual ERROR_USERNAME
    }

    @Test
    fun `given username is present, when password is blank, then status is password cannot be blank`() {
       val event = Login(username = "rob", password = "")
        logIn(event).status shouldBeEqual ERROR_PASSWORD
    }

    @Test
    fun `when username and password is present, then status is login success`() {
        val event = Login(username = "rob", password = "rob")
        logIn(event).status shouldBeEqual LOGIN_SUCCESS
    }
}
