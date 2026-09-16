package arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default

interface WindowScope {
    val windowScope: CoroutineScope
}

class WindowScopeProvider : WindowScope {
    override val windowScope: CoroutineScope = CoroutineScope(Default)
}
