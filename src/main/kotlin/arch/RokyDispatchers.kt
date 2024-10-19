package arch

import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import org.koin.java.KoinJavaComponent.get
import kotlin.coroutines.CoroutineContext

object RokyDispatchers {

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

    var Gui : CoroutineDispatcher = defaultGuiDispatcher

    fun resetToDefaultGuiDispatcher() {
        Gui = defaultGuiDispatcher
    }

}