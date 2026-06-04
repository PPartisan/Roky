package chatroom.users

import arch.RokyDispatchers
import chatroom.users.UsersViewState.Empty
import chatroom.users.UsersViewState.Users
import chatserver.*
import chatserver.profiles.SupabaseProfilesRepository.Profile
import coAnswersDelayed
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsersListPresenterTest {
    private lateinit var scope: CoroutineScope
    private lateinit var subscribePresence: SubscribeChatRepository
    private lateinit var subscribeProfiles: SubscribeChatRepository
    private lateinit var readPresence: ReadChatRepository<PresenceResult>
    private lateinit var readProfiles: ReadChatRepository<ProfileResult>
    private lateinit var displayableUsersList: DisplayableUsersList
    private lateinit var view: UsersListView
    private lateinit var presenter: UsersListPresenter

    @BeforeEach
    fun setUp() {
        view = mockk(relaxed = true)
        val repositories: ChatRepositories = mockk(relaxed = true)
        subscribePresence = mockk()
        readPresence = mockk()
        every { repositories.subscribePresence() } returns subscribePresence
        every { subscribePresence.subscribe() } just runs
        every { subscribePresence.unsubscribe() } just runs
        every { repositories.readPresence() } returns readPresence
        coEvery { readPresence.observe() } coAnswersDelayed { flowOf() }

        subscribeProfiles = mockk()
        readProfiles = mockk()
        every { repositories.subscribeProfiles() } returns subscribeProfiles
        every { subscribeProfiles.subscribe() } just runs
        every { subscribeProfiles.unsubscribe() } just runs
        every { repositories.readProfiles() } returns readProfiles
        coEvery { readProfiles.observe() } coAnswersDelayed { flowOf() }
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        displayableUsersList = DisplayableUsersList(repositories)
        scope = CoroutineScope(dispatcher)
        presenter = UsersListPresenter(UsersListTruncation(10), repositories, displayableUsersList, scope, dispatchers)
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
            coEvery { readPresence.observe() } coAnswersDelayed { flowOf(PresenceResult.ok(emptySet())) }
            coEvery { readProfiles.observe() } coAnswersDelayed { flowOf(ProfileResult.ok(emptyMap())) }
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
            val userProfileIds = setOf("1", "2", "3", "4")
            val userMap =
                mapOf(
                    "3" to Profile("3", "Robert"),
                    "1" to Profile("1", "Tom"),
                    "2" to Profile("2", "Allie"),
                    "4" to Profile("4", "Kai"),
                )
            coEvery { readPresence.observe() } coAnswersDelayed { flowOf(PresenceResult.ok(userProfileIds)) }
            coEvery { readProfiles.observe() } coAnswersDelayed { flowOf(ProfileResult.ok(userMap)) }
            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(Empty)
                view.show(
                    withArg {
                        assertTrue { it is Users }
                        assertTrue { (it as Users).users.toSet() == setOf("Robert", "Tom", "Allie", "Kai") }
                    },
                )
            }
        }

    @Test
    fun `when users list contains very long usernames, then truncate long usernames only`() =
        runTest(dispatcher) {
            val userIds = setOf("1", "2", "3")
            val userMap =
                mapOf(
                    "1" to Profile("1", "Allie"),
                    "2" to Profile("2", "Kai"),
                    "3" to Profile("3", "A very very long username"),
                )
            coEvery { readPresence.observe() } coAnswersDelayed { flowOf(PresenceResult.ok(userIds)) }
            coEvery { readProfiles.observe() } coAnswersDelayed { flowOf(ProfileResult.ok(userMap)) }
            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(Empty)
                view.show(
                    withArg {
                        assertTrue { it is Users }
                        assertTrue { (it as Users).users.toSet() == setOf("Allie", "Kai", "A very ve…") }
                    },
                )
            }
        }

    companion object {
        private val dispatcher = StandardTestDispatcher()
    }
}
