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
            with(logIn(event)) {
                this should haveMessage(ERROR_USERNAME)
                this should beFailure()
            }
        }

    @Test
    fun `given username is present, when password is blank, then status is password cannot be blank`() =
        runTest {
            val event = Login(username = "rob", password = "")
            with(logIn(event)) {
                this should haveMessage(ERROR_PASSWORD)
                this should beFailure()
            }
        }

    @Test
    fun `given username and password is present, when credentials are valid, then status is login success`() =
        runTest {
            coEvery { authenticator.isLoggedIn() } coAnswersDelayed { true }
            val event = Login(username = "rob", password = "rob")
            logIn(event) should beSuccessful()
        }
}

fun haveMessage(message: String) =
    Matcher<LoginUseCase.LoginResult> {
        MatcherResult(
            it.message == message,
            { "Result had message ${it.message}, but expected $message" },
            { "Result should not have message $message" },
        )
    }

fun beSuccessful() =
    Matcher<LoginUseCase.LoginResult> {
        MatcherResult(
            it.isSuccessful,
            { "Result was not successful but expected it to be successful" },
            { "Result should not have been successful" },
        )
    }

fun beFailure() =
    Matcher<LoginUseCase.LoginResult> {
        MatcherResult(
            !it.isSuccessful,
            { "Result was successful but expected it to be not successful" },
            { "Result should have been successful" },
        )
    }
