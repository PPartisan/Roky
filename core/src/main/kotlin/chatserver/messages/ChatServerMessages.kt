package chatserver.messages

import arch.RokyDispatchers
import chatserver.ChatRepositories
import chatserver.MessageCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

/**
 * Pure Business Logic Dependency Rules Module
 *
 * ARCHITECTURAL IMPROVEMENT: Granular Dependency Provisioning
 * Leverages the centralized ChatRepositories coordinator node to pull out narrow, individual
 * interface ports, routing them safely into our decoupled event broker.
 */
val chatServerMessagesModule = module {

    // Managed background context pool ensuring thread exceptions do not crash the application
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + get<RokyDispatchers>().default)
    }

    // Resolves the specialized ports granularly to satisfy the coordinator's isolated constructor
    single {
        val repositoryHub = get<ChatRepositories>()

        MessageCoordinator(
            messageReader = repositoryHub.readMessages(),
            appScope = get<CoroutineScope>()
        )
    }
}
