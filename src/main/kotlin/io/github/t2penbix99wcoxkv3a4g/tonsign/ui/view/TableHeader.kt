package io.github.t2penbix99wcoxkv3a4g.tonsign.ui.view

// https://github.com/olk90/compose-tableView/blob/main/src/main/kotlin/de/olk90/tableview/view/TableHeader.kt

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
annotation class TableHeader(
    val headerText: String,
    val columnIndex: Int
)

fun getTableHeader(annotations: List<Annotation>): TableHeader {
    val header = annotations.firstOrNull { a -> a is TableHeader }
        ?: throw IllegalArgumentException("Given annotations don't contain a TableHeader")
    return header as TableHeader
}