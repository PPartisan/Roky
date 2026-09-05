package chatroom.viewmessages

import chatserver.*
import chatserver.profiles.SupabaseProfilesRepository.Profile
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.SmartWrap
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class DisplayableMessagesTest {
    private lateinit var users: ReadChatRepository<ProfileResult>
    private lateinit var messages: ReadChatRepository<MessageResult>
    private lateinit var displayableMessages: DisplayableMessages

    @BeforeEach
    fun setUp() {
        users =
            mockk<ReadChatRepository<ProfileResult>>().also {
                every { it.latest() } returns
                    ProfileResult.ok(
                        emptyMap(),
                    )
            }
        messages = mockk<ReadChatRepository<MessageResult>>().also { every { it.observe() } returns flowOf() }
        val repository =
            mockk<ChatRepositories>().also {
                every { it.readProfiles() } returns users
                every { it.readMessages() } returns messages
            }
        displayableMessages = DisplayableMessages(repository, SmartWrap(50))
    }

    @Test
    fun `when known user sends message, and message is less than one line long, then show username and message`() =
        runTest {
            every { users.latest() } returns ProfileResult.ok(mapOf("id" to Profile("id", "Tony KnowsItAll")))
            every { messages.observe() } returns flowOf(MessageResult.ok(listOf(Message("id", "I will fail everyone"))))
            val emissions = mutableListOf<List<String>>()
            backgroundScope.launch { displayableMessages().collect(emissions::add) }
            advanceTimeBy(10.seconds)
            emissions[0].shouldContainExactly("Tony KnowsItAll: I will fail everyone")
        }

    @Test
    fun `when known user sends message, and message was sent with many short sentences, then show message split by receiver preferred line length`() =
        runTest {
            every { users.latest() } returns ProfileResult.ok(mapOf("id" to Profile("id", "Tony KnowsItAll")))
            every { messages.observe() } returns
                flowOf(
                    MessageResult.ok(
                        listOf(
                            Message(
                                "id",
                                "I will\nfail\neveryone\n\n",
                            ),
                        ),
                    ),
                )
            val emissions = mutableListOf<List<String>>()
            backgroundScope.launch { displayableMessages().collect(emissions::add) }
            advanceTimeBy(10.seconds)
            emissions[0].shouldContainExactly("Tony KnowsItAll: I will fail everyone")
        }

    @Test
    fun `when known user sends message, and message overflows max line size, then show message split by receiver preference`() =
        runTest {
            every { users.latest() } returns ProfileResult.ok(mapOf("id" to Profile("id", "Tony KnowsItAll")))
            every { messages.observe() } returns
                flowOf(
                    MessageResult.ok(
                        listOf(
                            Message(
                                "id",
                                "I will fail everyone because I hate all my students",
                            ),
                        ),
                    ),
                )
            val emissions = mutableListOf<List<String>>()
            backgroundScope.launch { displayableMessages().collect(emissions::add) }
            advanceTimeBy(10.seconds)
            emissions[0].shouldContainExactly(
                "Tony KnowsItAll: I will fail everyone because I ",
                "hate all my students",
            )
        }

    @Test
    fun `when unknown user post message, and message is less than one line long, then show anon message`() =
        runTest {
            every { users.latest() } returns ProfileResult.ok(emptyMap())
            every { messages.observe() } returns flowOf(MessageResult.ok(listOf(Message("id", "I will fail everyone"))))
            val emissions = mutableListOf<List<String>>()
            backgroundScope.launch { displayableMessages().collect(emissions::add) }
            advanceTimeBy(10.seconds)
            emissions[0].shouldContainExactly("anon: I will fail everyone")
        }

    @Test
    fun `when know user sends message, and all messages are less than one line long, and user changes their name, then show new name`() =
        runTest {
            every { users.latest() } returns ProfileResult.ok(mapOf("id" to Profile("id", "Tony KnowsItAll")))
            every { messages.observe() } returns
                flowOf(
                    MessageResult.ok(listOf(Message("id", "I will fail everyone"))),
                )
            val emissions = mutableListOf<List<String>>()
            backgroundScope.launch { displayableMessages().collect(emissions::add) }
            advanceTimeBy(10.seconds)

            every { users.latest() } returns ProfileResult.ok(mapOf("id" to Profile("id", "Neamah")))
            every { messages.observe() } returns
                flowOf(
                    MessageResult.ok(listOf(Message("id", "I hate my students..."))),
                )
            backgroundScope.launch { displayableMessages().collect(emissions::add) }
            advanceTimeBy(10.seconds)
            emissions[0].shouldContainExactly(
                "Tony KnowsItAll: I will fail everyone",
            )
            emissions[1].shouldContainExactly(
                "Neamah: I hate my students...",
            )
        }
}
