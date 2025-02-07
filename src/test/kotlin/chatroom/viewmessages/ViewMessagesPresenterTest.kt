package chatroom.viewmessages

import arch.RokyDispatchers
import chatroom.viewmessages.ViewMessagesViewState.Messages
import chatroom.viewmessages.ViewMessagesViewState.NoMessages
import coAnswersDelayed
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ViewMessagesPresenterTest {
    private lateinit var messages: ViewMessagesUseCase
    private lateinit var scope: CoroutineScope
    private lateinit var view: ViewMessagesView
    private lateinit var presenter: ViewMessagesPresenter

    @BeforeEach
    fun setUp() {
        messages = mockk(relaxed = true)
        coEvery { messages() } coAnswersDelayed { flowOf() }
        view = mockk(relaxed = true)
        view = mockk(relaxed = true)
        val dispatchers: RokyDispatchers =
            mockk<RokyDispatchers>().apply {
                every { main } returns dispatcher
                every { io } returns dispatcher
            }
        scope = CoroutineScope(dispatcher)
        presenter = ViewMessagesPresenter(scope, messages, dispatchers)
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
            coEvery { messages() } coAnswersDelayed { flowOf("Biggleswade is beautiful") }
            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(NoMessages)
                view.show(assertMessage("Biggleswade is beautiful"))
            }
        }

    @Test
    fun `given two messages exist, when attached, then show two messages`() =
        runTest(dispatcher) {
            coEvery { messages() } coAnswersDelayed {
                flowOf(
                    "Biggleswade is beautiful",
                    "Robert is good at Kotlin",
                )
            }
            presenter.attach(view)
            advanceUntilIdle()
            verifyOrder {
                view.show(NoMessages)
                view.show(assertMessage("Biggleswade is beautiful"))
                view.show(assertMessage("Robert is good at Kotlin"))
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
