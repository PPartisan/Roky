package chatroom.sendmessages

import arch.RokyDispatchers
import chatroom.sendmessages.SendMessageEvent.SendMessage
import chatroom.sendmessages.SendMessageViewState.Clear
import chatserver.MessagesRepository
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SendMessagePresenterTest {
    private lateinit var scope: CoroutineScope
    private lateinit var send: MessagesRepository.Write
    private lateinit var view: SendMessagesView
    private lateinit var presenter: SendMessagePresenter

    @BeforeEach
    fun setUp() {
        send = mockk()
        coEvery { send.send(any()) } just runs
        view = mockk(relaxed = true)
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        scope = CoroutineScope(dispatcher)
        presenter = SendMessagePresenter(scope, send, dispatchers)
    }

    @Test
    fun `when attached, then show clear view state`() {
        presenter.attach(view)
        verify { view.show(Clear) }
    }

    @Test
    fun `when send message event, then send message`() =
        runTest(dispatcher) {
            presenter.attach(view)
            presenter.onEvent(SendMessage("Hello"))
            advanceUntilIdle()
            coVerify { send.send("Hello") }
        }

    @Test
    fun `when send message event, then show clear view state`() =
        runTest(dispatcher) {
            presenter.attach(view)
            presenter.onEvent(SendMessage(""))
            advanceUntilIdle()
            verify { view.show(Clear) }
        }

    companion object {
        private val dispatcher = StandardTestDispatcher()
    }
}
