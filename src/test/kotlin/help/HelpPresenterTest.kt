package help

import arch.RokyDispatchers
import coAnswersDelayed
import help.FormattedTextRow.PlainText
import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HelpPresenterTest {
    private lateinit var scope: CoroutineScope
    private lateinit var view: HelpView
    private lateinit var page: suspend() -> HelpViewState
    private lateinit var presenter: HelpPresenter

    @BeforeEach
    fun setUp() {
        view = mockk(relaxed = true)
        val dispatchers : RokyDispatchers = mockk<RokyDispatchers>().apply {
            every { main } returns dispatcher
            every { io } returns dispatcher
        }
        scope = CoroutineScope(dispatcher)
        page = mockk()
        coEvery { page() } coAnswersDelayed { LoadingHelpViewState() }
        presenter = HelpPresenter(scope, dispatchers, page)
    }


    @Test
    fun `when attached, then immediately show loading`() = runTest(dispatcher) {
        presenter.attach(view)
        advanceUntilIdle()

        verify { view.show(withArg {
            it is LoadingHelpViewState
        }) }
    }

    @Test
    fun `when downloading page successful, then display page on UI`() = runTest(dispatcher) {
        val viewState = LoadedHelpViewState(listOf(PlainText("all good")))
        coEvery { page() } coAnswersDelayed { viewState }
        presenter.attach(view)
        advanceUntilIdle()

        verifyOrder {
            view.show(withArg { it is LoadingHelpViewState })
            view.show(viewState)
        }
    }

    companion object {
        private val dispatcher = StandardTestDispatcher()
    }
}