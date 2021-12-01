open class Tag(val name: String) {
    val children = mutableListOf<Tag>()
    val attributes = mutableListOf<Attribute>()
    override fun toString(): String {
        return "<$name" +
                (if (attributes.isEmpty()) "" else attributes.joinToString(separator = "", prefix = " ")) + ">" +
                    (if (children.isEmpty()) "" else children.joinToString(separator = "")) +
                    "</$name>"
    }
}
class Attribute(val name: String, val value: String) {
    override fun toString() = """$name="$value" """
}
fun <T : Tag> T.set(name: String, value: String?): T {
    if (value != null) {
        attributes.add(Attribute(name, value))
    }
    return this
}
fun <T : Tag> Tag.doInit(tag: T, init: T.() -> Unit): T {
    tag.init()
    children.add(tag)
    return tag
}

class Html : Tag("html")
class Table : Tag("table")
class Center : Tag("center")
class TR : Tag("tr")
class TD : Tag("td")
class Text(val text: String) : Tag("b") {
    override fun toString() = text
}

fun html(init: Html.() -> Unit): Html = Html().apply(init)
fun Html.table(init: Table.() -> Unit) = doInit(Table(), init)
fun Html.center(init: Center.() -> Unit) = doInit(Center(), init)
fun Table.tr(color: String? = null, init: TR.() -> Unit) = doInit(TR(), init)

fun TR.td(color: String? = null, align: String = "left", init: TD.() -> Unit) =
    doInit(TD(), init).set("align", align).set("bgcolor", color)
fun Tag.text(s: Any?) = doInit(Text(s.toString()), {})


/* DSL in versione semplificata*/
//open class Tag(val name: String) {
//    protected val children = mutableListOf<Tag>()
//
//    override fun toString() =
//        "<$name>${children.joinToString("")}</$name>"
//}
//
//fun table(init: TABLE.() -> Unit): TABLE {
//    val table = TABLE()
//    table.init()
//    return table
//}
//
//class TABLE : Tag("table") {
//    fun tr(init: TR.() -> Unit) {
//        val tr = TR()
//        tr.init()
//        children.add(tr)
//    }
//}
//
//class TR : Tag("tr") {
//    fun td(init: TD.() -> Unit) {
//        val td = TD()
//        td.init()
//        children.add(td)
//    }
//}
//
//class TD : Tag("td")
//
//fun createTable() =
//    table {
//        tr {
//            repeat(2) {
//                td {
//                }
//            }
//        }
//    }
//
//fun main() {
//    println(createTable())
//    //<table><tr><td></td><td></td></tr></table>
//}
