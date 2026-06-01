//package chatroom.users
//
//import arch.RokyDispatchers
//import chatroom.users.UsersViewState.Empty
//import chatroom.users.UsersViewState.Users
//import chatserver.*
//import coAnswersDelayed
//import io.mockk.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.advanceUntilIdle
//import kotlinx.coroutines.test.runTest
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class UsersListPresenterTest {
//    private lateinit var scope: CoroutineScope
//    private lateinit var subscribePresence: SubscribeChatRepository
//    private lateinit var readPresence: ReadChatRepository<PresenceResult>
//    private lateinit var view: UsersListView
//    private lateinit var presenter: UsersListPresenter
//
//    @BeforeEach
//    fun setUp() {
//        view = mockk(relaxed = true)
//        val repositories: ChatRepositories = mockk(relaxed = true)
//        subscribePresence = mockk()
//        readPresence = mockk()
//        every { repositories.subscribePresence() } returns subscribePresence
//        every { subscribePresence.subscribe() } just runs
//        every { subscribePresence.unsubscribe() } just runs
//        every { repositories.readPresence() } returns readPresence
//        coEvery { readPresence.observe() } coAnswersDelayed { flowOf() }
//        val dispatchers: RokyDispatchers =
//            mockk<RokyDispatchers>().apply {
//                every { main } returns dispatcher
//                every { io } returns dispatcher
//            }
//        scope = CoroutineScope(dispatcher)
//        presenter = UsersListPresenter(UsersListTruncation(10), repositories, scope, dispatchers)
//    }
//
//    @Test
//    fun `when attached, then show empty`() =
//        runTest(dispatcher) {
//            presenter.attach(view)
//            verify { view.show(Empty) }
//        }
//
//    @Test
//    fun `when users list is empty, then show empty list of users`() =
//        runTest(dispatcher) {
//            coEvery { readPresence.observe() } coAnswersDelayed { flowOf(PresenceResult.ok(emptySet())) }
//            presenter.attach(view)
//            advanceUntilIdle()
//            verifyOrder {
//                view.show(Empty)
//                view.show(
//                    withArg {
//                        assertTrue { it is Users }
//                        assertTrue { (it as Users).users.isEmpty() }
//                    },
//                )
//            }
//        }
//
//    @Test
//    fun `when users list is not empty, then show list of users`() =
//        runTest(dispatcher) {
//            val userSet = setOf("Robert", "Tom", "Allie", "Kai")
//            coEvery { readPresence.observe() } coAnswersDelayed { flowOf(PresenceResult.ok(userSet)) }
//            presenter.attach(view)
//            advanceUntilIdle()
//            verifyOrder {
//                view.show(Empty)
//                view.show(
//                    withArg {
//                        assertTrue { it is Users }
//                        assertTrue { (it as Users).users.toSet() == userSet }
//                    },
//                )
//            }
//        }
//
//    @Test
//    fun `when users list contains very long usernames, then truncate long usernames only`() =
//        runTest(dispatcher) {
//            val userSet = setOf("Allie", "Kai", "A very very long username")
//            coEvery { readPresence.observe() } coAnswersDelayed { flowOf(PresenceResult.ok(userSet)) }
//            presenter.attach(view)
//            advanceUntilIdle()
//            verifyOrder {
//                view.show(Empty)
//                view.show(
//                    withArg {
//                        assertTrue { it is Users }
//                        assertTrue { (it as Users).users == listOf("Allie", "Kai", "A very ve…") }
//                    },
//                )
//            }
//        }
//
//    companion object {
//        private val dispatcher = StandardTestDispatcher()
//    }
//}
