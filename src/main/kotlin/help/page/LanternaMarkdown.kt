package help.page

import com.vladsch.flexmark.ast.*
import com.vladsch.flexmark.util.ast.Node
import help.FormattedTextRow

object LanternaMarkdown : RenderMarkdown {
    override fun render(root: Node): List<FormattedTextRow> {
        val rows: MutableList<FormattedTextRow> = mutableListOf()
        render(root, rows)
        return rows
    }

    private fun String.truncate(maxCharsPerLine: Int = 30) =
        if (length > maxCharsPerLine) {
            replace(Regex("(.{1,$maxCharsPerLine})(\\s|$)"), "$1\n")
        } else {
            this
        }

    private fun render(
        node: Node,
        accumulator: MutableList<FormattedTextRow>,
    ) {
        if (isLeafNode(node)) {
            accumulator += toFormattedRow(node)
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

    private fun toFormattedRow(node: Node): FormattedTextRow =
        when (node) {
            is StrongEmphasis -> FormattedTextRow.Bold(node.text.toString().truncate())
            is Emphasis -> FormattedTextRow.Italic(node.text.toString().truncate())
            is HardLineBreak -> FormattedTextRow.LineBreak()
            is Heading -> FormattedTextRow.Header(node.text.toString().truncate())
            is Link -> FormattedTextRow.Hyperlink(node.text.toString().truncate(), node.url?.toString().orEmpty())
            is Text -> FormattedTextRow.PlainText(node.chars.toString().truncate())

            else -> throw IllegalArgumentException("Node is not a leaf node.")
        }

    private fun isLeafNode(node: Node): Boolean {
        return node::class in LEAF_CLASSES
    }
}
