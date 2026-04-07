package chatserver.messages

import arch.RokyDispatchers
import chatserver.Message
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class LocalChatMessagesTest {
    private lateinit var scope: CoroutineScope
    private lateinit var messages: LocalChatMessages

    @BeforeEach
    fun setUp() {
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        scope = CoroutineScope(dispatcher)
        messages = LocalChatMessages(dispatchers, scope)
    }

    @Test
    fun `given messages exist, when observing messages, then receive messages`() =
        runTest(dispatcher) {
            messages = LocalChatMessages(dispatchers, scope, kaimitter())
            backgroundScope.launch { messages.subscribe() }
            dispatcher.scheduler.advanceTimeBy(10.seconds)

            assertEquals(Message(USR, MSG_2), messages.latest().item.last())

            messages.unsubscribe()
        }

    @Test
    fun `when observing multiple messages, then receive same number of messages`() =
        runTest(dispatcher) {
            messages = LocalChatMessages(dispatchers, scope, kaimitter())

            val emissions = mutableListOf<List<Message>>()
            backgroundScope.launch {
                messages.observe().map { it.item }.collect(emissions::add)
            }
            messages.subscribe()

            advanceTimeBy(10.seconds)

            emissions.shouldHaveSize(3)
            messages.unsubscribe()
        }

    @Test
    fun `when observing multiple messages, then receive multiple messages`() =
        runTest(dispatcher) {
            messages = LocalChatMessages(dispatchers, scope, kaimitter())

            val emissions = mutableListOf<List<Message>>()
            backgroundScope.launch {
                messages.observe().map { it.item }.collect(emissions::add)
            }
            messages.subscribe()

            advanceTimeBy(10.seconds)
            val expected =
                listOf(
                    listOf(),
                    listOf(Message(USR, MSG_1)),
                    listOf(Message(USR, MSG_1), Message(USR, MSG_2)),
                )
            emissions.shouldContainInOrder(expected)
            messages.unsubscribe()
        }

    @Test
    fun `when writing message, then receive same message`() =
        runTest(dispatcher) {
            messages = LocalChatMessages(dispatchers, scope) { flowOf() }

            val emissions = mutableListOf<List<Message>>()
            backgroundScope.launch {
                messages.observe().map { it.item }.collect(emissions::add)
            }
            messages.subscribe()
            advanceTimeBy(10.seconds)
            messages.write("Barry is the smelliest.")

            advanceTimeBy(10.seconds)

            emissions.shouldContainInOrder(listOf(), listOf(Message("Me", "Barry is the smelliest.")))
            messages.unsubscribe()
        }

    companion object {
        private val dispatcher = StandardTestDispatcher()
        private val dispatchers: RokyDispatchers
            get() =
                mockk<RokyDispatchers>().apply {
                    every { main } returns dispatcher
                    every { io } returns dispatcher
                }

        private const val USR = "Kai"
        private const val CONTENT_1 = "Rob is bad!!"
        private const val CONTENT_2 = "Kai is worse!"
        private const val MSG_1 = CONTENT_1
        private const val MSG_2 = CONTENT_2

        private fun kaimitter(): () -> Flow<Message> =
            { flowOf(MSG_1, MSG_2).map { Message(USR, it) }.onEach { delay(1.seconds) } }
    }
}
