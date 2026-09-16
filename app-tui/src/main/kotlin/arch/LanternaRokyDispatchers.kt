package chatroom.arch

import arch.RokyDispatchers // Imports our pure domain interface port from :core
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import kotlin.coroutines.CoroutineContext

/**
 * Lanterna Terminal Interface Thread Dispatcher Integration Layer Adapter
 *
 * JUSTIFICATION FOR THE TEAM:
 * Implements the domain 'RokyDispatchers' contract, explicitly channeling 'main' execution
 * blocks safely onto Lanterna's dedicated background window rendering loop via 'invokeLater'.
 */
val lanternaDispatchersModule = module {
    factory { LanternaRokyDispatchersDelegate } bind RokyDispatchers::class
}

private data object LanternaRokyDispatchersDelegate : RokyDispatchers {
    private val gui: MultiWindowTextGUI by lazy {
        get(MultiWindowTextGUI::class.java)
    }

    private val defaultGuiDispatcher: CoroutineDispatcher by lazy {
        object : CoroutineDispatcher() {
            override fun dispatch(context: CoroutineContext, block: Runnable) {
                gui.guiThread.invokeLater(block)
            }
        }
    }

    override val io: CoroutineDispatcher get() = Dispatchers.IO
    override val default: CoroutineDispatcher get() = Dispatchers.Default
    override val unconfined: CoroutineDispatcher get() = Dispatchers.Unconfined
    override val main: CoroutineDispatcher get() = defaultGuiDispatcher
}

