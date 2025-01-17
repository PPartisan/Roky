package profile

import arch.RokyDispatchers
import coAnswersDelayed
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import profile.ProfileViewState.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfilePresenterTest {
    private lateinit var scope: CoroutineScope
    private lateinit var view: ProfileView
    private lateinit var requestUsername: RequestUserNameUseCase
    private lateinit var presenter: ProfilePresenter

    @BeforeEach
    fun setUp() {
        requestUsername = mockk()
        coEvery { requestUsername(any()) } coAnswersDelayed { Idle }

        view = mockk(relaxed = true)
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        scope = CoroutineScope(dispatcher)
        presenter = ProfilePresenter(scope, requestUsername, dispatchers)
    }

    @Test
    fun `given user requests new usernames, when request is successful, then show success status`() =
        runTest(dispatcher) {
            coEvery { requestUsername(any()) } coAnswersDelayed { Success("OKAYYY") }
            presenter.attach(view)
            advanceUntilIdle()
            presenter.onEvent(ProfileEvent.RequestUsername("USERNAMEE"))
            advanceUntilIdle()
            verifyOrder {
                view.show(Idle)
                view.show(Pending)
                view.show(
                    withArg {
                        assertTrue(it is Success)
                        assertTrue(it.status == "OKAYYY")
                    },
                )
            }
        }

    @Test
    fun `given user requests new username, when request fails, then show failed status`() =
        runTest(dispatcher) {
            coEvery { requestUsername(any()) } coAnswersDelayed { Failed("!OKAYYY") }
            presenter.attach(view)
            advanceUntilIdle()
            presenter.onEvent(ProfileEvent.RequestUsername("USERNAMEE"))
            advanceUntilIdle()
            verifyOrder {
                view.show(Idle)
                view.show(Pending)
                view.show(
                    withArg {
                        assertTrue(it is Failed)
                        assertTrue(it.status == "!OKAYYY")
                    },
                )
            }
        }

    @Test
    fun `when attached, then view status is idle`() =
        runTest(dispatcher) {
            presenter.attach(view)
            advanceUntilIdle()
            verify { view.show(Idle) }
        }

    companion object {
        private val dispatcher = StandardTestDispatcher()
    }
}
