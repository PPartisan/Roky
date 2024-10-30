package view

import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.Theme
import com.googlecode.lanterna.graphics.ThemeDefinition
import com.googlecode.lanterna.graphics.ThemeStyle
import com.googlecode.lanterna.gui2.Component
import com.googlecode.lanterna.gui2.ComponentRenderer
import com.googlecode.lanterna.gui2.WindowDecorationRenderer
import com.googlecode.lanterna.gui2.WindowPostRenderer
import java.util.*

val rokyTheme : Theme = object : Theme {
    override fun getDefaultDefinition(): ThemeDefinition {
        return rokyThemeDefinition
    }

    override fun getDefinition(clazz: Class<*>?): ThemeDefinition {
        return rokyThemeDefinition
    }

    override fun getWindowPostRenderer(): WindowPostRenderer? = null
    override fun getWindowDecorationRenderer(): WindowDecorationRenderer? = null
}

private val rokyThemeDefinition : ThemeDefinition = object : ThemeDefinition {
    // Define the normal style
    override fun getNormal(): ThemeStyle {
        return createCustomThemeStyle(
            background = "#607D8B",  // Dark background
            foreground = "#FFFFFF"   // White text
        )
    }

    // Define the pre-light style (for hover or focus state)
    override fun getPreLight(): ThemeStyle {
        return createCustomThemeStyle(
            background = "#CFD8DC",  // Lighter colour
            foreground = "#212121"   // Dark text
        )
    }

    // Define the selected style
    override fun getSelected(): ThemeStyle {
        return createCustomThemeStyle(
            background = "#CFD8DC",  // Lighter colour
            foreground = "#212121"   // Dark text
        )
    }

    // Define the active style
    override fun getActive(): ThemeStyle {
        return createCustomThemeStyle(
            background = "#CFD8DC",  // Lighter colour
            foreground = "#212121"   // Dark text
        )
    }

    // Define the insensitive (disabled) style
    override fun getInsensitive(): ThemeStyle {
        return createCustomThemeStyle(
            background = "#CFD8DC",  // Lighter colour
            foreground = "#757575"   // Lighter text
        )
    }

    override fun getCustom(name: String?): ThemeStyle = normal
    override fun getCustom(name: String?, defaultValue: ThemeStyle?): ThemeStyle = normal
    override fun getBooleanProperty(name: String?, defaultValue: Boolean): Boolean = defaultValue
    override fun isCursorVisible(): Boolean = true
    override fun getCharacter(name: String?, fallback: Char): Char = fallback
    override fun <T : Component?> getRenderer(type: Class<T>?): ComponentRenderer<T>? = null

    fun createCustomThemeStyle(background: String, foreground: String): ThemeStyle {
        return object: ThemeStyle {
            override fun getForeground(): TextColor {
                return TextColor.Factory.fromString(foreground)
            }

            override fun getBackground(): TextColor {
                return TextColor.Factory.fromString(background)
            }

            override fun getSGRs(): EnumSet<SGR> {
                return EnumSet.noneOf(SGR::class.java)
            }
        }
    }

}
