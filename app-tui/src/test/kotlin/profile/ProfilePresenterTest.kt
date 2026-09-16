package profile

import arch.RokyDispatchers
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import profile.ProfilePresenter.Companion.MESSAGE_OK
import profile.ProfileViewState.*

@OptIn(ExperimentalCoroutinesApi::class)
class ProfilePresenterTest {
    private lateinit var scope: CoroutineScope
    private lateinit var view: ProfileView
    private lateinit var usernames: MutableStateFlow<Map<String, String>>
    private lateinit var presenter: ProfilePresenter

    @BeforeEach
    fun setUp() {
        view = mockk(relaxed = true)
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        usernames = MutableStateFlow(emptyMap())
        val usernameRepository: ReadChatRepository<Map<String, String>> = mockk()
        every { usernameRepository.observe() } returns usernames
        every { usernameRepository.latest() } answers {
            usernames.value
        }
        val writeUsername: WriteChatRepository<String> = mockk()
        every { writeUsername.write(any()) } answers {
            val user = it.invocation.args[0] as String
            if (user == INVALID_USER) {
                // Keep mapping failure states via exceptions matching presentation errors
                throw RuntimeException()
            } else {
                val currentUsernames = usernames.value.toMutableMap()
                currentUsernames[user] = user
                usernames.value = currentUsernames
            }
        }
        scope = CoroutineScope(dispatcher)
        presenter = ProfilePresenter(scope, writeUsername, usernameRepository, dispatchers)
    }

    @Test
    fun `given user requests new usernames, when request is successful, then show success status`() =
        runTest(dispatcher) {
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
                        assertTrue(it.status == MESSAGE_OK)
                    }
                )
            }
        }

    @Test
    fun `given user requests new username, when request fails, then show failed status`() =
        runTest(dispatcher) {
            presenter.attach(view)
            advanceUntilIdle()
            presenter.onEvent(ProfileEvent.RequestUsername(INVALID_USER))
            advanceUntilIdle()
            verifyOrder {
                view.show(Idle)
                view.show(Pending)
                view.show(
                    withArg {
                        assertTrue(it is Failed)
                    }
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
        private const val INVALID_USER = "INVALID USER!"
    }
}
