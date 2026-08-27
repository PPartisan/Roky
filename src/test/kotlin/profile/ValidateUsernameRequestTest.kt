package profile

import chatserver.LoggedInUserId
import chatserver.ProfileResult
import chatserver.ReadChatRepository
import chatserver.WriteChatRepository
import chatserver.profiles.SupabaseProfilesRepository.Profile
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import profile.ValidateUsernameRequest.Result.*
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
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

    @Test
    fun `when logged in username is blank, then return LoggedInUsernameBlank`() =
        runTest {
            every { userId() } returns ""
            request("Kai") shouldBeEqual LoggedInUsernameBlank
        }

    @Test
    fun `when user requests username that matches their current username, then return UsernameUnchanged`() =
        runTest {
            every { userId() } returns "ka1"
            every { read.latest() } returns ProfileResult.ok(mapOf("ka1" to Profile(id = "ka1", username = "kai")))
            request("kai") shouldBeEqual UsernameUnchanged
        }

    @Test
    fun `when requesting valid username, and server does not respond, then return Timeout`() =
        runTest {
            every { userId() } returns "ka1"
            every { read.latest() } returns ProfileResult.ok(mapOf("ka1" to Profile(id = "ka1", username = "tom")))
            every { read.observe() } returns flow { (delay(5.seconds)) }
            val job =
                backgroundScope.launch {
                    request("kai") shouldBeEqual Timeout
                }
            advanceTimeBy(5.seconds)
            job.cancel()
        }

    @Test
    fun `when requesting valid username, and server accepts, then return Ok`() =
        runTest {
            every { userId() } returns "ka1"
            every { read.latest() } returns ProfileResult.ok(mapOf("ka1" to Profile(id = "ka1", username = "tom")))
            every { read.observe() } returns
                flow {
                    delay(1.seconds)
                    emit(ProfileResult.ok(mapOf("ka1" to Profile(id = "ka1", username = "kai"))))
                }
            val job =
                backgroundScope.launch {
                    request("kai") shouldBeEqual Ok
                }
            advanceTimeBy(5.seconds)
            job.cancel()
        }
}
