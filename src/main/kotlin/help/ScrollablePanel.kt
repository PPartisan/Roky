package help


import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.gui2.Direction.VERTICAL
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import kotlin.math.min

class ScrollablePanel (layout: LinearLayout) : Panel() {

    var scrollTop = 0    //how many children/rows are scrolled from the top

//    var first=0  //debugging
    var verticalScrollBar = ScrollBar(VERTICAL)

    val rowCount: Int get() = children.size     //total number of children in the panel

//    init {
//        syncBarToPanel()
//    }
    fun scrollTo(pos: Int) {
        val max = maxScroll()
        val newPos = pos.coerceIn(0, max)
        if (newPos != scrollTop) {
            scrollTop = newPos
//            onScrollChanged?.invoke(scrollTop, max)     //if onScrollChanges is null, nothing happens, otherwise invoke the function within
            verticalScrollBar.invalidate()
        }

    }

    private fun maxScroll(): Int = (rowCount - size.rows.coerceAtLeast(1)).coerceAtLeast(0)  //returns the last position the scrollbar could take. If I have 100 children(rows to display) and the screen size is 20 rows, then the last scroll position is 80

//    // let parent sync a ScrollBar when we scroll via keyboard
//    var onScrollChanged: ((pos: Int, max: Int) -> Unit)? = null   //onScrollChanged contains a function that takes 2 int and returns nothing


    override fun handleInput(key: KeyStroke): Boolean {
        print(key)
        val visible = size.rows.coerceAtLeast(1)
        val max = maxScroll()

        val before = scrollTop
        when (key.keyType) {
            KeyType.Character  -> {
                if (key.character=='w') {
                    scrollTo(scrollTop - 1)
                    println("w pressed:" + key + ". scrollTop= "+scrollTop)
                }
                if (key.character=='s') {
                    scrollTo(scrollTop + 1)
                    println("s pressed:" + key + ". scrollTop= "+scrollTop)
                }
                println(key)
            }
            KeyType.ArrowDown -> {
                println("Scrolltop was "+scrollTop)
                scrollTo(scrollTop + 1)
                println("ArrowDown: scrollTop is now "+scrollTop)
            }
            KeyType.PageUp    -> {
                scrollTo(scrollTop - visible)
                println(key)
            }
            KeyType.PageDown  -> {
                scrollTo(scrollTop + visible)
                println(key)
            }
            KeyType.Unknown -> println("Unkown: "+ key)
            KeyType.Home      -> scrollTo(0)
            KeyType.End       -> scrollTo(max)
            else -> {
                print("we're in the else")
                println(key)
                return super.handleInput(key)
            }
        }

        return scrollTop != before
    }



    override fun createDefaultRenderer(): ComponentRenderer<Panel> =
        object : ComponentRenderer<Panel> {

            override fun getPreferredSize(component: Panel): TerminalSize {
                var maxCols = 0
                val numberOfChildren = component.children.size
                for (child in component.children) {
                    val pref = child.preferredSize
                    if (pref.columns > maxCols) maxCols = pref.columns+1  //+1 to accomodate the scrollbar later
                }
                return TerminalSize(maxCols, numberOfChildren)
            }

            //In here we clear the viewpost, choose the slice of children, lay out and draw only the visible children, draw a vertical ScrollBar
            override fun drawComponent(g: TextGUIGraphics, component: Panel) {
                g.fill(' ')

                val avail = g.size
                val start = scrollTop
                val end = min(start + avail.rows, children.size) //smallest between how many children (rows to write) and how many rows we have in the terminal

                var y = 0
                for (i in start until end) {   //this is the slice of children currently visible
                    val child = children.elementAt(i) //take a child (a row of text) one at a time
//                    val width = min(child.preferredSize.columns, avail.columns-1) //if row of text is smaller than terminal width, take that, otherwise terminal width -1(scrollbar). But NB this means that if for some reason something has gone bad and the child is bigger, it will cut off the child
                    val width = getPreferredSize().columns
                    child.setPosition(TerminalPosition(0, y)) //tell the child where it should go
                    child.setSize(TerminalSize(width, 1)) //tell the child how big it is
                    val subG = g.newTextGraphics(child.position, child.size)
                    child.draw(subG)
                    y++ //next line
                }

                //here I'm missing a check if we even need the scrollbar to appear
                verticalScrollBar.onAdded(component.parent)
                val visible = size.rows.coerceAtLeast(0)
                verticalScrollBar.setViewSize(visible)            // number of visible rows
                verticalScrollBar.setScrollMaximum(rowCount)           // maximum scrollTop
                verticalScrollBar.setScrollPosition(scrollTop)
                verticalScrollBar.draw(
                    g.newTextGraphics(
                        TerminalPosition(g.getSize().getColumns() - 1, 0),
                        TerminalSize(1, g.getSize().getRows()))
                    )
            }

        }

//
}
