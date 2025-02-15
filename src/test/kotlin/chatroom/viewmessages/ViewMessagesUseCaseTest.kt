package chatroom.viewmessages

import chatserver.MessagesRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ViewMessagesUseCaseTest {
    private lateinit var messages: MessagesRepository.Read
    private lateinit var viewMessages: ViewMessagesUseCase

    @BeforeEach
    fun setUp() {
        messages = mockk()
        every {
            messages.observe()
        } returns flowOf()
    }

    @Test
    fun `given messages exist, when observing messages, then receive messages`() =
        runTest {
            viewMessages = ViewMessagesUseCase(listOf("Rob is a goodie twoshoes!!"), listOf("Brian"), messages)
            assertEquals(
                expected = "Brian: Rob is a goodie twoshoes!!",
                actual = viewMessages().first(),
            )
        }

    @Test
    fun `given messages exist, when observing multiple messages, then receive multiple messages`() =
        runTest {
            viewMessages = ViewMessagesUseCase(listOf("Rob is a goodie twoshoes!!"), listOf("Brian"), messages)
            val emissions = mutableListOf<String>()
            backgroundScope.launch { viewMessages().collect(emissions::add) }
            advanceTimeBy(10.seconds)
            assertEquals(
                expected = 3,
                actual = emissions.size,
            )
        }

    @Test
    fun `given messages exist, when messages interleave, then print all messages`() =
        runTest {
            every {
                messages.observe()
            } returns flowOf("Hi there you alright?!")
            viewMessages = ViewMessagesUseCase(listOf("Rob is a goodie twoshoes!!"), listOf("Brian"), messages)
            val emissions = mutableListOf<String>()
            backgroundScope.launch {
                viewMessages().collect(emissions::add)
            }
            advanceTimeBy(10.seconds)
            assertEquals(4, emissions.size)
            assertContains(emissions, "Me: Hi there you alright?!")
        }
}
