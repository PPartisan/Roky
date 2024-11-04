package view

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.LinearLayout.Alignment.Fill
import com.googlecode.lanterna.gui2.BorderLayout.Location.*
import com.googlecode.lanterna.gui2.LinearLayout.Alignment.Beginning
import com.googlecode.lanterna.gui2.LinearLayout.Alignment.End


fun <T: Component> T.linearLayoutFill() : T{
    return also {
        it.setLayoutData(LinearLayout.createLayoutData(Fill))
    }
}

fun Panel.paddingStart(cols: Int = 1) : Panel {
    val data = when(layoutManager) {
        is BorderLayout -> LEFT
        is LinearLayout -> LinearLayout.createLayoutData(Beginning)
        is GridLayout -> GridLayout.createLayoutData(GridLayout.Alignment.END, GridLayout.Alignment.FILL, false, true)
        else -> null
    }
    if(data == null) {
        //Unrecognised or unsupported layout manager
        return this
    }
    val empty = EmptySpace(TerminalSize(cols, 0)).setLayoutData(data)
    return addComponent(empty)
}

fun Panel.paddingEnd(cols: Int = 1) : Panel {
    val data = when(layoutManager) {
        is BorderLayout -> RIGHT
        is LinearLayout -> LinearLayout.createLayoutData(End)
        is GridLayout -> GridLayout.createLayoutData(GridLayout.Alignment.BEGINNING, GridLayout.Alignment.FILL, false, true)
        else -> null
    }
    if(data == null) {
        //Unrecognised or unsupported layout manager
        return this
    }
    val empty = EmptySpace(TerminalSize(cols, 0)).setLayoutData(data)
    return addComponent(empty)
}

fun Panel.paddingTop(rows: Int = 1) : Panel {
    val data = when(layoutManager) {
        is BorderLayout -> TOP
        is LinearLayout -> LinearLayout.createLayoutData(Beginning)
        is GridLayout -> GridLayout.createHorizontallyFilledLayoutData()
        else -> null
    }
    if(data == null) {
        //Unrecognised or unsupported layout manager
        return this
    }
    val empty = EmptySpace(TerminalSize(0, rows)).setLayoutData(data)
    return addComponent(empty)
}

fun Panel.paddingBottom(rows: Int = 1) : Panel {
    val data = when(layoutManager) {
        is BorderLayout -> BOTTOM
        is LinearLayout -> LinearLayout.createLayoutData(End)
        is GridLayout -> GridLayout.createHorizontallyFilledLayoutData()
        else -> null
    }
    if(data == null) {
        //Unrecognised or unsupported layout manager
        return this
    }
    val empty = EmptySpace(TerminalSize(0, rows)).setLayoutData(data)
    return addComponent(empty)
}

fun Panel.paddingHorizontal(cols: Int = 1) : Panel =
    paddingStart(cols).paddingEnd(cols)


fun Panel.paddingVertical(rows: Int = 1) : Panel =
    paddingTop(rows).paddingBottom(rows)

fun Panel.padding(size: Int = 1) : Panel =
    paddingHorizontal(size).paddingVertical(size)