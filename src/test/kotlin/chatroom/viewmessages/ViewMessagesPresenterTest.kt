package chatroom.viewmessages

import arch.RokyDispatchers
import chatroom.viewmessages.ViewMessagesViewState.Messages
import chatroom.viewmessages.ViewMessagesViewState.NoMessages
import chatserver.ChatMessageResult
import chatserver.ReadChatRepository
import chatserver.SubscribeChatRepository
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ViewMessagesPresenterTest {
    private lateinit var channel: SubscribeChatRepository
    private lateinit var read: ReadChatRepository<ChatMessageResult>
    private lateinit var scope: CoroutineScope
    private lateinit var view: ViewMessagesView
    private lateinit var presenter: ViewMessagesPresenter

    @BeforeEach
    fun setUp() {
        channel = mockk(relaxed = true)
        read = mockk()
        every { read.observe() } returns flowOf()
        view = mockk(relaxed = true)
        view = mockk(relaxed = true)
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        scope = CoroutineScope(dispatcher)
        presenter = ViewMessagesPresenter(scope, read, channel, dispatchers)
    }

    @Test
    fun `when attached, then show no messages`() =
        runTest(dispatcher) {
            presenter.attach(view)
            verifyOrder {
                view.show(NoMessages)
            }
        }

    @Test
    fun `given message exist, when attached, then show message`() =
        runTest(dispatcher) {
            every { read.observe() } returns flowOf(ChatMessageResult.ok("Biggleswade is bad"))
            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(NoMessages)
                view.show(assertMessage("Biggleswade is bad"))
            }
        }

    @Test
    fun `given message exist, and message is not ok, when attached, then show nothing`() =
        runTest(dispatcher) {
            every { read.observe() } returns flowOf(ChatMessageResult.fail(RuntimeException("Biggleswade is bad")))
            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(NoMessages)
            }
            verify(exactly = 0) {
                view.show(
                    withArg {
                        assertFalse { it !is Messages }
                    },
                )
            }
        }

    @Test
    fun `when attached, then subscribe to chat messages`() =
        runTest(dispatcher) {
            presenter.attach(view)
            verify { channel.subscribe() }
        }

    @Test
    fun `when detached, then unsubscribe to chat messages`() =
        runTest(dispatcher) {
            presenter.attach(view)
            presenter.detach()
            verify { channel.unsubscribe() }
        }

    @Test
    fun `given two messages exist, when attached, then show two messages`() =
        runTest(dispatcher) {
            every { read.observe() } returns
                flowOf(
                    ChatMessageResult.ok("Biggleswade is bad"),
                    ChatMessageResult.ok("Robert is good at bad, kai is better"),
                )

            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(NoMessages)
                view.show(assertMessage("Biggleswade is bad"))
                view.show(assertMessage("Robert is good at bad, kai is better"))
            }
        }

    private fun MockKVerificationScope.assertMessage(message: String): ViewMessagesViewState =
        withArg {
            assertTrue { it is Messages }
            assertEquals(
                message,
                (it as Messages).message,
            )
        }

    companion object {
        private val dispatcher = StandardTestDispatcher()
    }
}
