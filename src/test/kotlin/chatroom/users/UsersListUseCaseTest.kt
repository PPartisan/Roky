package chatroom.users

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class UsersListUseCaseTest {
    @Test
    fun `given list of users, when selecting users five times, then list size is five`() =
        runTest {
            var index = 0
            val users = listOf("Terry", "Matthew", "Rob", "Tom", "Kai", "Stefano")
            val useCase =
                UsersListUseCase(
                    users = users,
                    rndInt = { 6 },
                    rndUser = { it[index++ % it.size] },
                )
            val emissions = mutableListOf<List<String>>()
            val job =
                backgroundScope.launch {
                    useCase().collect(emissions::add)
                }
            advanceTimeBy(15.seconds)
            assertEquals(5, emissions.first().size)
            job.cancel()
        }

    @Test
    fun `given list of users, when selecting users five times, then emit sorted list of five users`() =
        runTest {
            var index = 0
            val users = listOf("Terry", "Matthew", "Rob", "Tom", "Kai", "Stefano")
            val useCase =
                UsersListUseCase(
                    users = users,
                    rndInt = { 6 },
                    rndUser = { it[index++ % it.size] },
                )
            val emissions = mutableListOf<List<String>>()
            val job =
                backgroundScope.launch {
                    useCase().collect(emissions::add)
                }
            advanceTimeBy(15.seconds)
            assertEquals(listOf("Kai", "Matthew", "Rob", "Terry", "Tom"), emissions.first())
            job.cancel()
        }
}
