package chatroom.viewmessages

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ViewMessagesUseCaseTest {
    private lateinit var viewMessages: ViewMessagesUseCase

    @Test
    fun `given messages exist, when observing messages, then receive messages`() =
        runTest {
            viewMessages = ViewMessagesUseCase(listOf("Rob is a goodie twoshoes!!"), listOf("Brian"))
            assertEquals(
                expected = "Brian: Rob is a goodie twoshoes!!",
                actual = viewMessages().first(),
            )
        }

    @Test
    fun `given messages exist, when observing multiple messages, then receive multiple messages`() =
        runTest {
            viewMessages = ViewMessagesUseCase(listOf("Rob is a goodie twoshoes!!"), listOf("Brian"))
            val emissions = mutableListOf<String>()
            backgroundScope.launch { viewMessages().collect(emissions::add) }
            advanceTimeBy(10.seconds)
            assertEquals(
                expected = 3,
                actual = emissions.size,
            )
        }
}
