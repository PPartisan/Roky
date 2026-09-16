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
import java.util.EnumSet

const val DEFAULT_TERMINAL_WIDTH = 40
const val DEFAULT_TERMINAL_HEIGHT = 20

val rokyTheme: Theme =
    object : Theme {
        override fun getDefaultDefinition(): ThemeDefinition {
            return rokyThemeDefinition
        }

        override fun getDefinition(clazz: Class<*>?): ThemeDefinition {
            return rokyThemeDefinition
        }

        override fun getWindowPostRenderer(): WindowPostRenderer? = null

        override fun getWindowDecorationRenderer(): WindowDecorationRenderer? = null
    }

private val rokyThemeDefinition: ThemeDefinition =
    object : ThemeDefinition {
        // Define the normal style
        override fun getNormal(): ThemeStyle {
            return createCustomThemeStyle(
                background = "#1E1E1E",
                foreground = "#00FF9C",
            )
        }

        // Define the pre-light style (for hover or focus state)
        override fun getPreLight(): ThemeStyle {
            return createCustomThemeStyle(
                background = "#2D2D2D",
                foreground = "#00FFB3",
            )
        }

        // Define the selected style
        override fun getSelected(): ThemeStyle {
            return createCustomThemeStyle(
                background = "#3D3D3D",
                foreground = "#00FFCC",
            )
        }

        // Define the active style
        override fun getActive(): ThemeStyle {
            return createCustomThemeStyle(
                background = "#1E1E1E",
                foreground = "#00FF9C",
            )
        }

        // Define the insensitive (disabled) style
        override fun getInsensitive(): ThemeStyle {
            return createCustomThemeStyle(
                background = "#1E1E1E",
                foreground = "#00FF9C",
            )
        }

        override fun getCustom(name: String?): ThemeStyle = normal

        override fun getCustom(
            name: String?,
            defaultValue: ThemeStyle?,
        ): ThemeStyle = normal

        override fun getBooleanProperty(
            name: String?,
            defaultValue: Boolean,
        ): Boolean = defaultValue

        override fun isCursorVisible(): Boolean = true

        override fun getCharacter(
            name: String?,
            fallback: Char,
        ): Char = fallback

        override fun <T : Component?> getRenderer(type: Class<T>?): ComponentRenderer<T>? = null

        fun createCustomThemeStyle(
            background: String,
            foreground: String,
            sgrs: EnumSet<SGR> = EnumSet.of(SGR.BOLD),
        ): ThemeStyle {
            return object : ThemeStyle {
                override fun getForeground(): TextColor {
                    return TextColor.Factory.fromString(foreground)
                }

                override fun getBackground(): TextColor {
                    return TextColor.Factory.fromString(background)
                }

                override fun getSGRs(): EnumSet<SGR> {
                    return sgrs
                }
            }
        }
    }
