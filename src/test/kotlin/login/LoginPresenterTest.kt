package login

import arch.RokyDispatchers
import authentication.AuthState
import authentication.ReadAuth
import coAnswersDelayed
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import login.LoginEvent.Login
import login.LoginPresenter.Companion.AUTHENTICATING
import login.LoginUseCase.LoginResult
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
    private lateinit var auth: ReadAuth
    private lateinit var presenter: LoginPresenter

    @BeforeEach
    fun setUp() {
        logIn = mockk(relaxed = true)
        coEvery { logIn(any()) } coAnswersDelayed { LoginResult.fail("") }

        view = mockk(relaxed = true)
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        scope = CoroutineScope(dispatcher)
        auth = mockk()
        every {
            auth.state()
        } returns flowOf()
        presenter = LoginPresenter(scope, logIn, auth, dispatchers)
    }

    @Test
    fun `when attached, then view state is idle`() =
        runTest(dispatcher) {
            presenter.attach(view)
            advanceUntilIdle()
            verify { view.show(Idle()) }
        }

    @Test
    fun `when login, then immediately show init status`() =
        runTest(dispatcher) {
//            coEvery { logIn(any()) } coAnswersDelayed { Idle() }
            every {
                auth.state()
            } returns flowOf(AuthState.InitState)
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
            coEvery { logIn(any()) } coAnswersDelayed { LoginResult.fail("Couldn't sign in") }

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
                view.show(
                    withArg {
                        assertTrue(it is Idle)
                    },
                )
            }
        }

    companion object {
        private val dispatcher = StandardTestDispatcher()
    }
}
