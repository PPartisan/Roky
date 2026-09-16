package chatroom.users

import chatroom.users.DisplayableUsersList.Companion.UNKNOWN_USER
import chatserver.ChatRepositories
import chatserver.ReadChatRepository
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class DisplayableUsersListTest {
    private lateinit var readPresence: ReadChatRepository<Set<String>>
    private lateinit var readProfiles: ReadChatRepository<Map<String, String>>
    private lateinit var users: DisplayableUsersList

    @BeforeEach
    fun setUp() {
        readPresence = mockk()
        readProfiles = mockk()
        every { readPresence.observe() } returns flowOf()
        every { readProfiles.observe() } returns flowOf()
        val chatRepositories = mockk<ChatRepositories>()
        every { chatRepositories.readPresence() } returns readPresence
        every { chatRepositories.readProfiles() } returns readProfiles
        users = DisplayableUsersList(chatRepositories)
    }

    @Test
    fun `when username exist for profile id, then emit username`() =
        runTest {
            every { readPresence.observe() } returns flowOf(setOf("id"))
            every { readProfiles.observe() } returns flowOf(mapOf("id" to "user"))

            val emissions = mutableListOf<List<String>>()
            backgroundScope.launch {
                users().collect(emissions::add)
            }
            advanceTimeBy(10.seconds)
            emissions shouldHaveSize 1
            emissions shouldHaveSingleElement listOf("user")
        }

    @Test
    fun `when username does not exist, then user is unknown`() =
        runTest {
            every { readPresence.observe() } returns flowOf(setOf("id"))
            every { readProfiles.observe() } returns flowOf(mapOf())

            val emissions = mutableListOf<List<String>>()
            backgroundScope.launch {
                users().collect(emissions::add)
            }
            advanceTimeBy(10.seconds)
            emissions shouldHaveSize 1
            emissions shouldHaveSingleElement listOf(UNKNOWN_USER)
        }

    @Test
    fun `when username for profile id is blank, then user is unknown`() =
        runTest {
            every { readPresence.observe() } returns flowOf(setOf("id"))
            every { readProfiles.observe() } returns flowOf(mapOf("id" to ""))

            val emissions = mutableListOf<List<String>>()
            backgroundScope.launch {
                users().collect(emissions::add)
            }
            advanceTimeBy(10.seconds)
            emissions shouldHaveSize 1
            emissions shouldHaveSingleElement listOf(UNKNOWN_USER)
        }

    @Test
    fun `when multiple users are in the chatroom, then show usernames for users in chatroom`() =
        runTest {
            every { readPresence.observe() } returns flowOf(setOf("kai-id", "martine-id"))
            every { readProfiles.observe() } returns flowOf(
                mapOf(
                    "kai-id" to "kai",
                    "martine-id" to "martine",
                    "steve-id" to "steve",
                    "terry-id" to "terry",
                    "olive-id" to "olive"
                )
            )
            val emissions = mutableListOf<List<String>>()
            backgroundScope.launch {
                users().collect(emissions::add)
            }
            advanceTimeBy(10.seconds)
            emissions shouldHaveSize 1
            emissions shouldHaveSingleElement listOf("kai", "martine")
        }
}
