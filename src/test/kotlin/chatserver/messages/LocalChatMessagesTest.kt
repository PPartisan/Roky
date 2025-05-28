package chatserver.messages

import arch.RokyDispatchers
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.string.shouldStartWith
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
            messages = LocalChatMessages(dispatchers, scope, listOf("Rob is bad!!"), listOf("Kai"), )
            backgroundScope.launch { messages.subscribe()}
            dispatcher.scheduler.advanceTimeBy(10.seconds)

            assertEquals("Kai: Rob is bad!!", messages.latest().item )

            messages.unsubscribe()
        }

    @Test
    fun `when observing multiple messages, then receive same number of messages`() =
        runTest(dispatcher) {
            messages = LocalChatMessages(dispatchers, scope, listOf("Rob is bad!!", "Kai is good!"), listOf("Kai"), )
            val emissions = mutableListOf<String>()
            backgroundScope.launch {
                messages.subscribe()
                messages.observe().map { it.item }.collect(emissions::add)
            }

            dispatcher.scheduler.advanceTimeBy(10.seconds)
            messages.unsubscribe()

            assertEquals(2, emissions.size)


        }

    @Test
    fun `when observing multiple messages, then receive multiple messages`() =
        runTest(dispatcher) {
            val content = listOf("Rob is bad!!", "Kai is good!")
            messages = LocalChatMessages(dispatchers, scope, content, listOf("Kai"), {emitImmediately("Kai", content)} )
            val emissions = mutableListOf<String>()
            backgroundScope.launch {
                messages.observe().map { it.item }.collect(emissions::add)
                messages.subscribe()
            }

            dispatcher.scheduler.advanceTimeBy(10.seconds)



            emissions.shouldContainInOrder(
                "",
                "Kai: Rob is bad!!",
                "Kai: Kai is good!"
            )

            messages.unsubscribe()


        }


    companion object {
        private val dispatcher = StandardTestDispatcher()
        private val dispatchers: RokyDispatchers
            get() = mockk<RokyDispatchers>().apply {
                        every { main } returns dispatcher
                        every { io } returns dispatcher
            }
        private fun emitImmediately(user:String, messages:List<String> ) = flow<String> {
            messages.map {"$user: $it"} .forEach{emit(it)}

        }

    }

}
