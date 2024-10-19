package arch

import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.get
import kotlin.coroutines.CoroutineContext

val rokyDispatchersModule = module {
    factory { RokyDispatchersDelegate } bind RokyDispatchers::class
}

sealed interface RokyDispatchers {
    val io : CoroutineDispatcher
    val default : CoroutineDispatcher
    val unconfined : CoroutineDispatcher
    val main : CoroutineDispatcher
}

private data object RokyDispatchersDelegate : RokyDispatchers {

    private val gui : MultiWindowTextGUI by lazy {
        get(MultiWindowTextGUI::class.java)
    }

    private val defaultGuiDispatcher : CoroutineDispatcher by lazy {
        object : CoroutineDispatcher() {
            override fun dispatch(context: CoroutineContext, block: Runnable) {
                gui.guiThread.invokeLater(block)
            }
        }
    }

    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default
    override val unconfined: CoroutineDispatcher
        get() = Dispatchers.Unconfined
    override val main: CoroutineDispatcher
        get() = defaultGuiDispatcher

}