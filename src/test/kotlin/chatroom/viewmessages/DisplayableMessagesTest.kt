package chatroom.viewmessages

import chatserver.ChatRepositories
import chatserver.MessageResult
import chatserver.ProfileResult
import chatserver.ReadChatRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach

class DisplayableMessagesTest {
    private lateinit var users: ReadChatRepository<ProfileResult>
    private lateinit var messages: ReadChatRepository<MessageResult>
    private lateinit var displayableMessages: DisplayableMessages

    @BeforeEach
    fun setUp() {
        users = mockk<ReadChatRepository<ProfileResult>>().also { every { it.observe() } returns flowOf() }
        messages = mockk<ReadChatRepository<MessageResult>>().also { every { it.observe() } returns flowOf() }
        val repository =
            mockk<ChatRepositories>().also {
                every { it.readProfiles() } returns users
                every { it.readMessages() } returns messages
            }
        displayableMessages = DisplayableMessages(repository)
    }
}
