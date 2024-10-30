package view

import arch.WindowScope
import arch.WindowScopeProvider
import com.googlecode.lanterna.gui2.BasicWindow
import com.googlecode.lanterna.gui2.Window
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import navigation.NavigateToMainMenu

class AppWindow (
    private val window: BasicWindow,
    private val menu: NavigateToMainMenu
) :Window by window, WindowScope by WindowScopeProvider() {

    constructor(title:String, menu: NavigateToMainMenu):this(BasicWindow(title), menu)

    override fun handleInput(key: KeyStroke?): Boolean {
        if (key?.keyType==KeyType.Escape){
            menu.toMainMenu()
            return true
        }

        return window.handleInput(key)
    }

    override fun close() {
        window.close()
    }
}
