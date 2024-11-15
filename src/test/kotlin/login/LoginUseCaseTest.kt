package login

import authentication.Authenticator
import coAnswersDelayed
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import login.LoginEvent.*
import login.LoginUseCase.Companion.ERROR_PASSWORD
import login.LoginUseCase.Companion.ERROR_USERNAME
import login.LoginUseCase.Companion.LOGIN_FAILURE
import login.LoginUseCase.Companion.LOGIN_SUCCESS
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginUseCaseTest {
    private lateinit var logIn: LoginUseCase
    private lateinit var authenticator: Authenticator

    @BeforeEach
    fun setUp() {
        authenticator = mockk(relaxed = true)
        coEvery { authenticator.login(any(), any()) } coAnswersDelayed { false }
        logIn = LoginUseCase(authenticator)
    }

    @Test
    fun `when username is blank, then status is username cannot be blank`() = runTest {
        val event = Login(username = "", password = "")
        logIn(event).status shouldBeEqual ERROR_USERNAME
    }

    @Test
    fun `given username is present, when password is blank, then status is password cannot be blank`() = runTest {
       val event = Login(username = "rob", password = "")
        logIn(event).status shouldBeEqual ERROR_PASSWORD
    }

    @Test
    fun `given username and password is present, when credentials are valid, then status is login success`() = runTest {
        coEvery { authenticator.login(any(), any()) } coAnswersDelayed {true}
        val event = Login(username = "rob", password = "rob")
        logIn(event).status shouldBeEqual LOGIN_SUCCESS
    }

    @Test
    fun `given username and password is present, when credentials are invalid, then status is login failure`() = runTest {
        coEvery { authenticator.login(any(), any()) } coAnswersDelayed {false}
        val event = Login(username = "rob", password = "rob")
        logIn(event).status shouldBeEqual LOGIN_FAILURE
    }

}
