package arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.SupervisorJob

interface WindowScope : CoroutineScope

class WindowScopeProvider : WindowScope, CoroutineScope by CoroutineScope(Default + SupervisorJob())
