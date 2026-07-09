package about

import com.github.ppartisan.roky.BuildConfig
import com.googlecode.lanterna.SGR.*
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.Direction.VERTICAL
import com.googlecode.lanterna.gui2.Window.Hint.CENTERED
import help.FormattedTextRow.*
import navigation.NavigateToMainMenu
import view.*
import java.awt.GridLayout

class AboutWindow(
    menu: NavigateToMainMenu,
) : AppWindow("About", menu) {
    init {
        setHints(listOf(CENTERED))
        val label =
            Panel(
                GridLayout(1).apply {
                    topMarginSize = 1
                    bottomMarginSize = 1
                    leftMarginSize = 2
                    rightMarginSize = 2
                },
            ).apply {

                addComponent(Label("ROKY v${BuildConfig.VERSION}"))
            }
        val root =
            Panel(LinearLayout(VERTICAL))
                .apply {
                    addComponent(label)
                }

        component = root
    }
}
