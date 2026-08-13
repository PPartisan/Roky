package profile

import chatserver.LoggedInUserId
import chatserver.ProfileResult
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import profile.ValidateUsernameRequest.Result.UsernameBlank

class ValidateUsernameRequestTest {
    private lateinit var write: WriteChatRepository<String>
    private lateinit var read: ReadChatRepository<ProfileResult>
    private lateinit var userId: LoggedInUserId

    private lateinit var request: ValidateUsernameRequest

    @BeforeEach
    fun setUp() {
        write =
            mockk<WriteChatRepository<String>>().also {
                every { it.write(any()) } just runs
            }
        read =
            mockk<ReadChatRepository<ProfileResult>>().also {
                every { it.latest() } returns ProfileResult.ok(emptyMap())
                every { it.observe() } returns flowOf()
            }
        userId =
            mockk<LoggedInUserId>().also {
                every { it() } returns "Logged in user"
            }

        request = ValidateUsernameRequest(write, read, userId)
    }

    @Test
    fun `when username blank, then return UsernameBlank`() =
        runTest {
            request("") shouldBeEqual UsernameBlank
        }
}
