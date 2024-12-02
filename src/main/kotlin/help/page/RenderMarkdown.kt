package help.page

import com.vladsch.flexmark.util.ast.Node
import help.FormattedTextRow

interface RenderMarkdown {
    fun render (root:Node): List <FormattedTextRow>

}
