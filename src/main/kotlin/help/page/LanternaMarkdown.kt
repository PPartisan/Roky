package help.page

import com.vladsch.flexmark.ast.*
import com.vladsch.flexmark.util.ast.Node
import help.FormattedTextRow
import utils.smartWrap


object LanternaMarkdown : RenderMarkdown {
    override fun render(root: Node): List<FormattedTextRow> {
        val rows: MutableList<FormattedTextRow> = mutableListOf()
        render(root, rows)
        return rows
    }

    private fun render(
        node: Node,
        accumulator: MutableList<FormattedTextRow>,
    ) {
        if (isLeafNode(node)) {
            accumulator += toFormattedRow(node)   //if what toFormattedTextRow outcomes is a list, not a FormattedTextRow anymore, this still works because in Kotlin X += iterable is the same as X + iterable(0) + iterable(1)...
        } else {
            node.children.forEach { child ->
                render(child, accumulator)
            }
        }
    }

    private val LEAF_CLASSES =
        setOf(
            StrongEmphasis::class,
            Emphasis::class,
            Text::class,
            HardLineBreak::class,
            Heading::class,
            HardLineBreak::class,
            Link::class,
        )

//    private fun toFormattedRow(node: Node): FormattedTextRow =  //sometimes a node is longer than a single line
//        when (node) {
//            is StrongEmphasis -> FormattedTextRow.Bold(node.text.toString().smartWrap())
//            is Emphasis -> FormattedTextRow.Italic(node.text.toString().smartWrap())
//            is HardLineBreak -> FormattedTextRow.LineBreak()
//            is Heading -> FormattedTextRow.Header(node.text.toString().smartWrap())
//            is Link -> FormattedTextRow.Hyperlink(node.text.toString().smartWrap(), node.url?.toString().orEmpty())
//            is Text -> FormattedTextRow.PlainText(node.chars.toString().smartWrap())
//            else -> throw IllegalArgumentException("Node is not a leaf node.")
//        }


    private fun toFormattedRow(node: Node): MutableList<FormattedTextRow> {  //I make it return a list because this way you can use it for the help page wrapping too.
        //Since a single node could be more than one line, if I return the text as a single string with various \n inside, when I give it to the accumulator, that will be
        //considered still a single string, and will render as a single component. Because of how I'm trying to write the ScrollablePanel, I need to have each row as a separate
//          component when sent to render. I'm sure there's a more delicate and more abstract way of handling this, but as I'm experimenting this was the fastest solution I found
        var listOfRows: MutableList<FormattedTextRow> = mutableListOf()
        when (node) {
            is StrongEmphasis -> for (line in node.text.toString().smartWrap().split(Regex("\n"))) {
                listOfRows.add(FormattedTextRow.Bold(line))
            }
            is Emphasis -> for (line in node.text.toString().smartWrap().split(Regex("\n"))) {
                listOfRows.add(FormattedTextRow.Italic(line))
            }
            is HardLineBreak -> FormattedTextRow.LineBreak()
            is Heading -> for (line in node.text.toString().smartWrap().split(Regex("\n"))) {
                listOfRows.add(FormattedTextRow.Header(line))
            }
            is Link -> for (line in node.text.toString().smartWrap().split(Regex("\n"))) {
                listOfRows.add(FormattedTextRow.Hyperlink(line, node.url?.toString().orEmpty()))
            }
            is Text -> for (line in node.chars.toString().smartWrap().split(Regex("\n"))) {
                listOfRows.add(FormattedTextRow.PlainText(line))
            }

            else -> throw IllegalArgumentException("Node is not a leaf node.")
        }
        return listOfRows
    }

    private fun isLeafNode(node: Node): Boolean {
        return node::class in LEAF_CLASSES
    }
}
