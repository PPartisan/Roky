package chatserver.messages

import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class LocalChatMessagesTest {
//    private lateinit var scope: CoroutineScope
//    private lateinit var messages: LocalChatMessages
//
//    @BeforeEach
//    fun setUp() {
//        val dispatchers: RokyDispatchers =
//            mockk<RokyDispatchers>().apply {
//                every { main } returns dispatcher
//                every { io } returns dispatcher
//            }
//        scope = CoroutineScope(dispatcher)
//        messages = LocalChatMessages(dispatchers, scope)
//    }
//
//    @Test
//    fun `given messages exist, when observing messages, then receive messages`() =
//        runTest(dispatcher) {
//            messages = LocalChatMessages(dispatchers, scope, kaimitter())
//            backgroundScope.launch { messages.subscribe() }
//            dispatcher.scheduler.advanceTimeBy(10.seconds)
//
//            assertEquals(MSG_2, messages.latest().item)
//
//            messages.unsubscribe()
//        }
//
//    @Test
//    fun `when observing multiple messages, then receive same number of messages`() =
//        runTest(dispatcher) {
//            messages = LocalChatMessages(dispatchers, scope, kaimitter())
//
//            val emissions = mutableListOf<String>()
//            backgroundScope.launch {
//                messages.observe().map { it.item }.collect(emissions::add)
//            }
//            messages.subscribe()
//
//            advanceTimeBy(10.seconds)
//
//            emissions.shouldHaveSize(3)
//            messages.unsubscribe()
//        }
//
//    @Test
//    fun `when observing multiple messages, then receive multiple messages`() =
//        runTest(dispatcher) {
//            messages = LocalChatMessages(dispatchers, scope, kaimitter())
//
//            val emissions = mutableListOf<String>()
//            backgroundScope.launch {
//                messages.observe().map { it.item }.collect(emissions::add)
//            }
//            messages.subscribe()
//
//            advanceTimeBy(10.seconds)
//
//            emissions.shouldContainInOrder("", MSG_1, MSG_2)
//            messages.unsubscribe()
//        }
//
//    @Test
//    fun `when writing message, then receive same message`() =
//        runTest(dispatcher) {
//            messages = LocalChatMessages(dispatchers, scope) { flowOf() }
//
//            val emissions = mutableListOf<String>()
//            backgroundScope.launch {
//                messages.observe().map { it.item }.collect(emissions::add)
//            }
//            messages.subscribe()
//            advanceTimeBy(10.seconds)
//            messages.write("Barry is the best.")
//
//            advanceTimeBy(10.seconds)
//
//            emissions.shouldContainInOrder("", "Me: Barry is the best.")
//            messages.unsubscribe()
//        }
//
//    companion object {
//        private val dispatcher = StandardTestDispatcher()
//        private val dispatchers: RokyDispatchers
//            get() =
//                mockk<RokyDispatchers>().apply {
//                    every { main } returns dispatcher
//                    every { io } returns dispatcher
//                }
//
//        private const val USR = "Kai"
//        private const val CONTENT_1 = "Rob is bad!!"
//        private const val CONTENT_2 = "Kai is worse!"
//        private const val MSG_1 = "$USR: $CONTENT_1"
//        private const val MSG_2 = "$USR: $CONTENT_2"
//
//        private fun kaimitter() = { flowOf(MSG_1, MSG_2).onEach { delay(1.seconds) } }
//    }
}
