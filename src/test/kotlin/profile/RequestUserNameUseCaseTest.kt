package profile

import coAnswersDelayed
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.string.shouldStartWith
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import profile.ProfileViewState.Failed
import profile.ProfileViewState.Success
import profile.RequestUserNameUseCase.Companion.ERROR_USERNAME_BLANK
import profile.RequestUserNameUseCase.RequestUserName

class RequestUserNameUseCaseTest {
    private lateinit var requestUserName: RequestUserName
    private lateinit var useCase: RequestUserNameUseCase

    @BeforeEach
    fun setUp() {
        requestUserName = mockk()
        coEvery { requestUserName(any()) } coAnswersDelayed {
            firstArg<String>() == VALID_USER
        }
        useCase = RequestUserNameUseCase(requestUserName)
    }

    @Test
    fun `when username is empty, then request username failed`() = runTest {
        assertTrue(useCase("") is Failed)
    }

    @Test
    fun `when username is empty, then show username cannot be blank message`() = runTest {
        useCase("").status shouldBeEqual ERROR_USERNAME_BLANK
    }

    @Test
    fun `when username is invalid, then request username failed`() = runTest {
        assertTrue(useCase(INVALID_USER) is Failed)
    }

    @Test
    fun `when username is invalid, then show username invalid message`() = runTest {
        useCase(INVALID_USER).status shouldStartWith "Could not change username"
    }

    @Test
    fun `when username is valid, then request username success`() = runTest {
        assertTrue(useCase(VALID_USER) is Success)
    }

    @Test
    fun `when username is valid, then show username changed message`() = runTest {
        useCase(VALID_USER).status shouldStartWith "Changed username to"
    }

    companion object {
        private const val VALID_USER = "valid_user"
        private const val INVALID_USER = "invalid_user"
    }
}
