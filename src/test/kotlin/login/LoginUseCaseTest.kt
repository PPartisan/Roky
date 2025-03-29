package login

import authentication.Auth
import coAnswersDelayed
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import login.LoginEvent.Login
import login.LoginUseCase.Companion.ERROR_PASSWORD
import login.LoginUseCase.Companion.ERROR_USERNAME
import login.LoginUseCase.Companion.LOGIN_FAILURE
import login.LoginUseCase.Companion.LOGIN_SUCCESS
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoginUseCaseTest {
    private lateinit var logIn: LoginUseCase
    private lateinit var authenticator: Auth

    @BeforeEach
    fun setUp() {
        authenticator = mockk(relaxed = true)
        coEvery { authenticator.isLoggedIn() } coAnswersDelayed { false }
        logIn = LoginUseCase(authenticator)
    }

    @Test
    fun `when username is blank, then status is username cannot be blank`() =
        runTest {
            val event = Login(username = "", password = "")
            logIn(event) should haveStatus(ERROR_USERNAME)
        }

    @Test
    fun `given username is present, when password is blank, then status is password cannot be blank`() =
        runTest {
            val event = Login(username = "rob", password = "")
            logIn(event) should haveStatus(ERROR_PASSWORD)
        }

    @Test
    fun `given username and password is present, when credentials are valid, then status is login success`() =
        runTest {
            coEvery { authenticator.isLoggedIn() } coAnswersDelayed { true }
            val event = Login(username = "rob", password = "rob")
            logIn(event) should haveStatus(LOGIN_SUCCESS)
        }

    @Test
    fun `given username and password is present, when credentials are invalid, then status is login failure`() =
        runTest {
            coEvery { authenticator.isLoggedIn() } coAnswersDelayed { false }
            val event = Login(username = "rob", password = "rob")
            logIn(event) should haveStatus(LOGIN_FAILURE)
        }
}

fun haveStatus(status: String) =
    Matcher<LoginViewState> {
        MatcherResult(
            it.status == status,
            { "View state had status ${it.status}, but expected $status" },
            { "View state should not have status $status" },
        )
    }
