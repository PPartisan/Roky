package chatroom.users

import arch.RokyDispatchers
import chatroom.users.UsersViewState.Empty
import chatroom.users.UsersViewState.Users
import coAnswersDelayed
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsersListPresenterTest {
    private lateinit var scope: CoroutineScope
    private lateinit var listUsers: UsersListUseCase
    private lateinit var view: UsersListView
    private lateinit var presenter: UsersListPresenter

    @BeforeEach
    fun setUp() {
        view = mockk(relaxed = true)
        listUsers = mockk(relaxed = true)
        coEvery { listUsers() } coAnswersDelayed { flowOf() }
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        scope = CoroutineScope(dispatcher)
        presenter = UsersListPresenter(listUsers, UsersListTruncation(10), scope, dispatchers)
    }

    @Test
    fun `when attached, then show empty`() =
        runTest(dispatcher) {
            presenter.attach(view)
            verify { view.show(Empty) }
        }

    @Test
    fun `when users list is empty, then show empty list of users`() =
        runTest(dispatcher) {
            coEvery { listUsers() } coAnswersDelayed { flowOf(emptyList()) }
            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(Empty)
                view.show(
                    withArg {
                        assertTrue { it is Users }
                        assertTrue { (it as Users).users.isEmpty() }
                    },
                )
            }
        }

    @Test
    fun `when users list is not empty, then show list of users`() =
        runTest(dispatcher) {
            val userList = listOf("Robert", "Tom", "Allie", "Kai")
            coEvery { listUsers() } coAnswersDelayed { flowOf(userList) }
            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(Empty)
                view.show(
                    withArg {
                        assertTrue { it is Users }
                        assertTrue { (it as Users).users == userList }
                    },
                )
            }
        }

    @Test
    fun `when users list contains very long usernames, then truncate long usernames only`() =
        runTest(dispatcher) {
            val userList = listOf("Allie", "Kai", "A very very long username")
            coEvery { listUsers() } coAnswersDelayed { flowOf(userList) }
            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(Empty)
                view.show(
                    withArg {
                        assertTrue { it is Users }
                        assertTrue { (it as Users).users == listOf("Allie", "Kai", "A very ve…") }
                    },
                )
            }
        }

    companion object {
        private val dispatcher = StandardTestDispatcher()
    }
}
