package login

import arch.RokyDispatchers
import coAnswersDelayed
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import login.LoginEvent.Login
import login.LoginPresenter.Companion.AUTHENTICATING
import login.LoginViewState.Authenticating
import login.LoginViewState.Idle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginPresenterTest {
    private lateinit var scope: CoroutineScope
    private lateinit var logIn: LoginUseCase
    private lateinit var view: LoginView
    private lateinit var presenter: LoginPresenter

    @BeforeEach
    fun setUp() {
        logIn = mockk(relaxed = true)
        coEvery { logIn(any()) } coAnswersDelayed { Idle() }

        view = mockk(relaxed = true)
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        scope = CoroutineScope(dispatcher)
        presenter = LoginPresenter(scope, logIn, dispatchers)
    }

    @Test
    fun `when attached, then view state is idle`() =
        runTest(dispatcher) {
            presenter.attach(view)
            advanceUntilIdle()
            verify { view.show(Idle()) }
        }

    @Test
    fun `when login, then immediately show authenticating status`() =
        runTest(dispatcher) {
            coEvery { logIn(any()) } coAnswersDelayed { Idle() }

            presenter.attach(view)
            presenter.onEvent(Login("User", "Password"))
            advanceUntilIdle()

            verifyOrder {
                view.show(Idle())
                view.show(
                    withArg {
                        assertTrue { it.status == AUTHENTICATING }
                    },
                )
            }
        }

    @Test
    fun `when login, then show outcome of login`() =
        runTest(dispatcher) {
            val loginFailed = Idle("User", "", "Login Failed")
            coEvery { logIn(any()) } coAnswersDelayed { loginFailed }

            presenter.attach(view)
            presenter.onEvent(Login("User", "Password"))
            advanceUntilIdle()

            verifyOrder {
                view.show(Idle())
                view.show(
                    withArg {
                        assertTrue(it is Authenticating)
                    },
                )
                view.show(loginFailed)
            }
        }

    companion object {
        private val dispatcher = StandardTestDispatcher()
    }
}
