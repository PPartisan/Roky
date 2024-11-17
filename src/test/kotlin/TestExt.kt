import io.mockk.Call
import io.mockk.MockKAdditionalAnswerScope
import io.mockk.MockKAnswerScope
import io.mockk.MockKStubScope
import kotlinx.coroutines.delay

infix fun <T,B> MockKStubScope<T, B>.coAnswersDelayed(
    answer: suspend MockKAnswerScope<T, B>.(Call) -> T
): MockKAdditionalAnswerScope<T, B> = coAnswers {
    delay(1)
    answer(it)
}
