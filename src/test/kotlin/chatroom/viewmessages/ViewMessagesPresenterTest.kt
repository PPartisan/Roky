package chatroom.viewmessages

import arch.RokyDispatchers
import chatroom.viewmessages.ViewMessagesViewState.Messages
import chatroom.viewmessages.ViewMessagesViewState.NoMessages
import io.mockk.MockKVerificationScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ViewMessagesPresenterTest {
    private lateinit var read: DisplayableMessages
    private lateinit var scope: CoroutineScope
    private lateinit var view: ViewMessagesView
    private lateinit var presenter: ViewMessagesPresenter

    @BeforeEach
    fun setUp() {
        read = mockk()
        every { read() } returns flowOf()
        view = mockk(relaxed = true)
        view = mockk(relaxed = true)
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        scope = CoroutineScope(dispatcher)
        presenter = ViewMessagesPresenter(scope, read, dispatchers)
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
            every { read() } returns flowOf(listOf("Kai: Biggleswad ez badd"))
            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(NoMessages)
                view.show(assertMessage("Kai: Biggleswad ez badd"))
            }
        }

    @Test
    fun `given two messages exist, when attached, then show two messages`() =
        runTest(dispatcher) {
            every { read() } returns
                flowOf(
                    listOf("Kai: Pokemon good"),
                    listOf("Allie: Barry is the best"),
                )

            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(NoMessages)
                view.show(assertMessage("Kai: Pokemon good"))
                view.show(assertMessage("Allie: Barry is the best"))
            }
        }

    private fun MockKVerificationScope.assertMessage(message: String): ViewMessagesViewState {
        return withArg {
            assertTrue { it is Messages }
            assertEquals(
                message,
                (it as Messages).lines[0],
            )
        }
    }

    companion object {
        private val dispatcher = StandardTestDispatcher()
    }
}
