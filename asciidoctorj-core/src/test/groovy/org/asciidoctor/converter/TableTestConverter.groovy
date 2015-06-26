package org.asciidoctor.converter

import org.asciidoctor.ast.AbstractNode
import org.asciidoctor.ast.Cell
import org.asciidoctor.ast.DocumentRuby
import org.asciidoctor.ast.Table

class TableTestConverter extends AbstractConverter {

    public static final int COLWIDTH = 15
    public static final String COL_SEPARATOR = '|'
    public static final String NEWLINE = '\n'

    TableTestConverter(String backend, Map<Object, Object> opts) {
        super(backend, opts)
    }

    @Override
    Object convert(AbstractNode node, String transform, Map<Object, Object> opts) {
        if (transform == null) {
            transform = node.nodeName
        }

        this."convert${transform.capitalize()}"(node)
    }

    Object convertEmbedded(DocumentRuby node) {
        String result = node.doctitle.toUpperCase()
        String content = node.content
        if (content && content.trim().length() > 0) {
            result += NEWLINE * 2 + content
        }
        result
    }

    Object convertTable(Table table) {

        Closure cellFormatter = { Cell cell ->
            if (cell.style == 'asciidoc') {
                cell.content.toString().padRight(COLWIDTH)
            } else {
                switch (cell.column.horizontalAlignment) {
                    case Table.HorizontalAlignment.LEFT:
                        cell.text.padRight(COLWIDTH)
                        break
                    case Table.HorizontalAlignment.CENTER:
                        cell.text.center(COLWIDTH)
                        break
                    case Table.HorizontalAlignment.RIGHT:
                        cell.text.padLeft(COLWIDTH)
                        break
                }
            }
        }


        (1..table.columns.size()).each {
            assert table.columns[it - 1].columnNumber == it
        }

        def tableRows = []

        if (table.header.size() > 0) {
            tableRows << table.header[0].cells.collect(cellFormatter).join(COL_SEPARATOR)
            tableRows << table.header[0].cells.collect { '-' * COLWIDTH }.join('+')
        }

        tableRows << table.body.collect { row -> row.cells.collect(cellFormatter).join(COL_SEPARATOR) }

        if (table.footer.size() > 0) {
            tableRows << table.footer[0].cells.collect { '=' * COLWIDTH }.join('#')
            tableRows << table.footer[0].cells.collect(cellFormatter).join(COL_SEPARATOR)
        }

        tableRows.flatten().join(NEWLINE)
    }


}
