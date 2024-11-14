package login

import arch.RokyDispatchers
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import login.LoginViewState.Idle
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginPresenterTest {
    private lateinit var scope: CoroutineScope
    private lateinit var logIn: LoginUseCase
    private lateinit var view: LoginView
    private lateinit var presenter: LoginPresenter

    @BeforeEach
    fun setUp() {
        logIn = mockk(relaxed = true)

        view = mockk(relaxed = true)
        val dispatchers : RokyDispatchers = mockk<RokyDispatchers>().apply {
            every { main } returns dispatcher
        }
        scope = CoroutineScope(dispatcher)
        presenter = LoginPresenter(scope,logIn, dispatchers)
    }

    @Test
    fun `when attached, then view state is idle`() = runTest(dispatcher) {
        presenter.attach(view)
        advanceUntilIdle()
        verify{ view.show(Idle()) }
    }

    companion object {
        private val dispatcher = StandardTestDispatcher()
    }
}
