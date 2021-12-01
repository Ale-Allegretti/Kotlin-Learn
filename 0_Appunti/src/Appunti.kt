import java.util.*
import kotlin.NoSuchElementException
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.random.Random
import kotlin.reflect.KProperty

/* STRING TEMPLATES */
val question = "life, the universe, and everything";
val answer = 42;

val tripleQuotedString = """
    #question = "$question"
    #answer = $answer""".trimMargin("#")

fun main1() {
    println(tripleQuotedString)
}


/* LAMBDA EXP */
fun containsEven(collection: Collection<Int>): Boolean =
    collection.any {
            numero:Int -> numero%2 == 0
    }


/* CLASS
 * il prefisso "data" permette al costruttore (intrinseco) di generare in automatico equals/hashCode e toString
 */
data class Person(val name: String, var age: Int)


/* IS, WHEN e FOR (..in) */
fun main2() {
    var x: Any = "ciao"

    // vale la prima condizione per cui non fallisce il confronto
    when (x) {
        !is Int -> print("x is not an integer")
        in 1..10 -> print("x is in the range")
        !in 10..20 -> print("x is outside the range")
        else -> print("none of the above")
    }

}


/* ESTENSIONI STATICHE e COMPANION */
class Example {
    fun printFunctionType() { println("Class method") }
}
fun Example.printFunctionType(i: Int) { println("Extension function #$i") }

class MyClass {
    companion object { }  // will be called "Companion"
}
fun MyClass.Companion.printCompanion() { println("companion") }

fun main3() {

    Example().printFunctionType(1)

    MyClass.printCompanion()
}


/* COMPARABLE e RANGE */
data class MyDate(val year: Int, val month: Int, val dayOfMonth: Int) : Comparable<MyDate> {
    override fun compareTo(other: MyDate) = when {
        year != other.year -> year - other.year
        month != other.month -> month - other.month
        else -> dayOfMonth - other.dayOfMonth
    }
}
fun test(date1: MyDate, date2: MyDate) {
    println(date1 < date2)
}
//fun checkInRange(date: MyDate, first: MyDate, last: MyDate): Boolean {
//    return date in first..last
//}


// è possibile iterare su oggetti puchè sia disponibile un iterator come membro o estensione
class DateRange(val start: MyDate, val end: MyDate) : Iterable<MyDate> {
    override fun iterator(): Iterator<MyDate> {
        return object : Iterator<MyDate> {
            var current: MyDate = start

            override fun next(): MyDate {
                if (!hasNext()) throw NoSuchElementException()
                val result = current
                current = current.followingDate()
                return result
            }

            override fun hasNext(): Boolean = current <= end
        }
    }
}

enum class TimeInterval { DAY, WEEK, YEAR }

// per il task1
operator fun MyDate.plus(timeInterval: TimeInterval): MyDate = this.addTimeIntervals(timeInterval, 1)
// per il task2
operator fun MyDate.plus(timeIntervals: RepeatedTimeInterval) =
    addTimeIntervals(timeIntervals.timeInterval, timeIntervals.number)

class RepeatedTimeInterval(val timeInterval: TimeInterval, val number: Int)

operator fun TimeInterval.times(number: Int) =
    RepeatedTimeInterval(this, number)
//


fun task1(today: MyDate): MyDate {
    return today + TimeInterval.YEAR + TimeInterval.WEEK
}
fun task2(today: MyDate): MyDate {
    return today + TimeInterval.YEAR * 2 + TimeInterval.WEEK * 3 + TimeInterval.DAY * 5
}

fun main4() {
    val date1 = MyDate(2021, 2, 4)
    val date2 = MyDate(2021, 3, 4)
    val date3 = MyDate(2022, 3, 22)
    test(date1, date2)  // true
//    println(checkInRange(date3, date1, date2)) // false

    val dateRange = DateRange(date1, date2)
    println(date3 in dateRange)

    println(task1(MyDate(2021, 11, 29)))
    println(task2(MyDate(2021, 11, 29)))
}


/* INVOKE */

class Invokable {
    var numberOfInvocations: Int = 0
        private set

    operator fun invoke(): Invokable {
        numberOfInvocations++
        return this
    }
}

fun invokeTwice(invokable: Invokable) = invokable()()



/* COLLECTION e ordinamenti */
val strings = listOf("bbb", "a", "cc")

/*
 Se il compilatore può analizzare la firma senza parametri, non è necessario dichiarare il parametro e "->"
 può essere omesso. Il parametro verrà dichiarato implicitamente con il nome "it"
*/
fun main5() {
    println(strings.sortedByDescending { it.length })
}

val numbers = setOf(1, 2, 3)
val numbersMap = mapOf("key1" to 1, "key2" to 2, "key3" to 3, "key11" to 11)
val colors = listOf("red", "brown", "grey")
val animals = listOf("fox", "bear", "wolf")
val numbersLett = listOf("one", "two", "three", "four")

val names = listOf("Alice Adams", "Brian Brown", "Clara Campbell")
data class FullName (val firstName: String, val lastName: String)
// funzione ausiliaria per il print di cui sotto
fun parseFullName(fullName: String): FullName {
    val nameParts = fullName.split(" ")
    if (nameParts.size == 2) {
        return FullName(nameParts[0], nameParts[1])
    } else throw Exception("Wrong name format")
}

val numberSets = listOf(setOf(1, 2, 3), setOf(4, 5, 6), setOf(1, 2))

val numbersList = (1..100).toList()

val filteredIdx = numbersLett.filterIndexed { index, s -> (index != 0) && (s.length < 5)  }
val filteredNot = numbersLett.filterNot { it.length <= 3 }

val numbersMix = listOf(null, 1, "two", 3.0, "four")

val words = "The quick brown fox jumps over the lazy dog".split(" ")



fun main6() {
    /* MAP */
    fun Shop.getCustomerCities(): Set<City> = customers.map { it.city }.toSet()
    fun Shop.getCustomersFrom(city: City): List<Customer> = customers.filter { it.city == city }
    println(numbers.mapNotNull { if ( it == 2) null else it * 3 })
    println(numbers.mapIndexedNotNull { idx, value -> if (idx == 0) null else value * idx })

    println(numbersMap.mapKeys { it.key.uppercase() })
    println(numbersMap.mapValues { it.value + it.key.length })
    //

    /* ZIP */
    println(colors.zip(animals) { color, animal -> "The ${animal.replaceFirstChar { it.uppercase() }} is $color"})


    /* ASSOCIATE */
    fun Shop.nameToCustomerMap(): Map<String, Customer> =
         customers.associate {it.name to it}   // oppure
         /*associateBy(Customer::name)*/           // ritornano una Map<String, Customer>

    fun Shop.customerToCityMap(): Map<Customer, City> =
         customers.associate {it to it.city}       // oppure
         /*customers.associateWith(Customer::city)*/   // ritornano una Map<Customer, City>

    println(numbersLett.associateBy(keySelector = { it.first().uppercaseChar() }, valueTransform = { it.length }))
    println(names.associate { name -> parseFullName(name).let { it.lastName to it.firstName } })
    //

    /* FLATTEN */
    fun Customer.getOrderedProducts(): List<Product> =
         orders.flatMap { it.products }         // ritorna lista prodotti ordered

    fun Shop.getOrderedProducts(): Set<Product> =
        customers.flatMap(Customer::getOrderedProducts).toSet()
                          // ritorna set prodotti ordinati da almeno un venditore
    println(numberSets.flatten())
    //

    /* JOINTOSTRING */
    println(numbers.joinToString(limit = 10, truncated = "<...>"))


    /* FILTER, PARTITION e GROUPBY */
    println(filteredIdx)
    println(filteredNot)

    numbersMix.filterIsInstance<String>().forEach {
        println(it.uppercase())
    }
    numbersMix.filterNotNull().forEach {
        println(it)   // length is unavailable for nullable Strings
    }

     fun Shop.getCustomersWithMoreUndeliveredOrders(): Set<Customer> =
        customers.filter {
            val (delivered, undelivered) = it.orders.partition { it.isDelivered }
            undelivered.size > delivered.size
        }.toSet()                               // ritorna il venditore con più non-spediti che spediti

    val (match, rest) = numbers.partition { it > 3 }
    println(match)
    println(rest)

     fun Shop.groupCustomersByCity(): Map<City, List<Customer>> =
        customers.groupBy { it.city }   // ritorna una Map
                                        // cioè la lista di venditori di una città
    //


    /* MAX/MIN(BY) e SUM(OF) */
     fun getMostExpensiveProductBy(customer: Customer): Product? =
        customer.orders
            .flatMap(Order::products)
            .maxByOrNull(Product::price)    // ritorna il prodotto più costoso ordinato dal cliente

     fun moneySpentBy(customer: Customer): Double =
        customer.orders
            .flatMap(Order::products)
            .sumOf(Product::price)          // ritorna la somma del totale speso
    //


    /* REDUCE/FOLD */
    fun Shop.getProductsOrderedByAll(): Set<Product> {
        val allProducts = customers.flatMap { it.getOrderedProducts() }.toSet()
        return customers.fold(allProducts) { orderedByAll, customer ->
            orderedByAll.intersect(customer.getOrderedProducts().toSet())
        }
    }                                       // ritorna un set di tutti i prodotti ordinati


    fun findMostExpensiveProductBy(customer: Customer): Product? {
        val ordiniSpediti: List<Order> = customer.orders.filter { it.isDelivered }
        return ordiniSpediti.flatMap(Order::products)
                            .maxByOrNull(Product::price)
    }

    fun Shop.getNumberOfTimesProductWasOrdered(product: Product): Int {
        return customers
                .flatMap(Customer::getOrderedProducts)
                .count { it == product }
    }


    /* SEQUENCE */

    //convert the List to a Sequence
    val wordsSequence = words.asSequence()
    val lengthsSequence = wordsSequence.filter { println("filter: $it"); it.length > 3 }
        .map { println("length: ${it.length}"); it.length }
        .take(4)

    println(lengthsSequence.toList())


    /* ESEMPIO GENERALE */
    // PRIMA :
    fun doSomethingWithCollectionOldStyle(collection: Collection<String>): Collection<String>? {
        val groupsByLength = mutableMapOf<Int, MutableList<String>>()
        for (s in collection) {
            var strings: MutableList<String>? = groupsByLength[s.length]
            if (strings == null) {
                strings = mutableListOf()
                groupsByLength[s.length] = strings
            }
            strings.add(s)
        }

        var maximumSizeOfGroup = 0
        for (group in groupsByLength.values) {
            if (group.size > maximumSizeOfGroup) {
                maximumSizeOfGroup = group.size
            }
        }

        for (group in groupsByLength.values) {
            if (group.size == maximumSizeOfGroup) {
                return group
            }
        }
        return null
    }
    // DOPO :
    fun doSomethingWithCollection(collection: Collection<String>): Collection<String>? {

        val groupsByLength = collection.groupBy { s -> s.length }

        val maximumSizeOfGroup = groupsByLength.values.map { group -> group.size }.maxOrNull()

        return groupsByLength.values.firstOrNull { group -> group.size == maximumSizeOfGroup }
    }
}


/* PROPERTIES */
class PropertyExample() {
    var counter = 0
    var propertyWithCounter: Int? = null
        set(value) {
            field = value
            counter++           // ogni volta assegnato un valore a PropertyExample.propertyWithCounter
        }
}

class LazyProperty(val initializer: () -> Int) {
    var value: Int? = null
    val lazy: Int
        get() {
            if (value == null) {
                value = initializer()
            }
            return value!!
        }
}




/* DELEGATE */
class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}

class ExampleDel {
    var p: String by Delegate()
}

//  Delegates.observable() accetta due argomenti: il valore iniziale e un gestore per le modifiche.
class User {
    var name: String by Delegates.observable("<no name>") {
            prop, old, new ->
        println("$old -> $new")
//        println("Property: $prop")
    }
}

// Per delegare una proprietà a un'altra proprietà, utilizzare il "::" nel nome del delegato
class MyClassDel {
    var newName: Int = 0
    @Deprecated("Use 'newName' instead", ReplaceWith("newName"))
    var oldName: Int by this::newName
}

// creare delegati come oggetti anonimi senza creare nuove classi,
// utilizzando le interfacce ReadOnlyProperty e ReadWriteProperty dalla libreria standard di Kotlin
fun resourceDelegate(): ReadWriteProperty<Any?, Int> =
    object : ReadWriteProperty<Any?, Int> {
        var curValue = 0
        override fun getValue(thisRef: Any?, property: KProperty<*>): Int = curValue
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            curValue = value
        }
    }

val readOnly: Int by resourceDelegate()  // ReadWriteProperty as val
var readWrite: Int by resourceDelegate()



class DateToEf {
    var date: MyDate by EffectiveDate()
}
/* l'esempio (a mio parere) è un po' fuorviante in quanto l'esercizio usa un Delegate per estrarre e costruire
*  una data, passando dai millesecondi (con metodi di DateUtil.kt).
*  Questo utilizzo si capisce bene nel passaggio di println(timeInMillis) */
class EffectiveDate<R> : ReadWriteProperty<R, MyDate> {

    var timeInMillis: Long? = null

    override fun getValue(thisRef: R, property: KProperty<*>): MyDate {
        println(timeInMillis)
        return timeInMillis!!.toDate()
    }

    override fun setValue(thisRef: R, property: KProperty<*>, value: MyDate) {
        timeInMillis = value.toMillis()
        println(timeInMillis)
    }
}


fun main7() {
    val e = ExampleDel()
    println(e.p)
    e.p = "NEW"     // print intrinseca (vedi su)

    val user = User()
    user.name = "first"
    user.name = "second"    // print intrinseca (vedi su)

    val myClass = MyClassDel()
    // Notification: 'oldName: Int' is deprecated.
    // Use 'newName' instead
    myClass.oldName = 42
    println(myClass.newName)


    println("\nDelegato ReadOnly e ReadWrite")
    println(readOnly)
    readWrite = 10
    println(readWrite)
    println()


    var dataEffettiva : DateToEf = DateToEf()
    dataEffettiva.date = MyDate(2021, 11, 30)
    println(dataEffettiva.date)
}


/* FUNZIONI LETTERALI */
fun task(): List<Boolean> {
    val isEven: Int.() -> Boolean = { this%2 == 0 }
    val isOdd: Int.() -> Boolean = { this%2 != 0 }

    return listOf(42.isOdd(), 239.isOdd(), 294823098.isEven())      // ritornano il rispettivo risultato boolean
}

// permette dunque di creare una HashMap mutabile
fun <K, V> buildMutableMap(build: HashMap<K, V>.() -> Unit): Map<K, V> {
    val map = HashMap<K, V>()
    map.build()
    return map
}

fun usage(): Map<Int, String> {
    return buildMutableMap {
        put(0, "0")
        for (i in 1..10) {
            put(i, "$i")
        }
    }
}


/* FUNZIONI SCOPE */
class MultiportService(var url: String, var port: Int) {    // per il RUN di cui sotto
    fun prepareRequest(): String = "Default request"
    fun query(request: String): String = "Result for query '$request'"
}
fun main8() {
    /* es. di utilizzo di APPLY e ALSO */
    val numberList = mutableListOf<Double>()
    numberList.also { println("Populating the list") }
        .apply {
            add(2.71)
            add(3.14)
            add(1.0)
        }
        .also { println("Sorting the list") }
        .sort()

    // permette di creare tramite un apply una qualsiasi delle (due) funzioni che seguono
    fun <T> T.myApply(f: T.() -> Unit): T {
        f()
        return this
    }
    fun createString(): String {
        return StringBuilder().myApply {
            append("Numbers: ")
            for (i in 1..10) {
                append(i)
            }
        }.toString()
    }
    fun createMap(): Map<Int, String> {
        return hashMapOf<Int, String>().myApply {
            put(0, "0")
            for (i in 1..10) {
                put(i, "$i")
            }
        }
    }


    /* es. di utilizzo di IT */
    fun getRandomInt(): Int {
        return Random.nextInt(100).also {
            println("getRandomInt() generated value $it")
        }
    }
    val i = getRandomInt()


    /* es. di utilizzo di RUN */
    val numbers = mutableListOf("one", "two", "three")
    val countEndsWithE = numbers.run {
        add("four")
        add("five")
        count { it.endsWith("e") }
    }
    println("There are $countEndsWithE elements that end with e.")

    val service = MultiportService("https://example.kotlinlang.org", 80)
    val result = service.run {
        port = 8080
        query(prepareRequest() + " to port $port")
    }


    /* es di utilizzo di LET */
    fun processNonNullString(str: String) {}
    val str: String? = "Hello"
    val length = str?.let {
        println("let() called on $it")
        processNonNullString(it)      // OK: 'it' is not null inside '?.let { }'
        it.length
    }


    /* es di utilizzo di WITH */
    val firstAndLast = with(numbers) {
        "The first element is ${first()}," +
                " the last element is ${last()}"
    }
    println(firstAndLast)


    /* es di utilizzo di TAKE(IF)/(UNLESS) */
    fun displaySubstringPosition(input: String, sub: String) {
        input.indexOf(sub).takeIf { it >= 0 }?.let {
            println("The substring $sub is found in $input.")
            println("Its start position is $it.")
        }
    }
    displaySubstringPosition("010000011", "11")
    displaySubstringPosition("010000011", "12")
}


/* HTML BUILDERS -> vedi HTML.kt e Data.kt */
fun renderProductTable(): String {
    return html {
        table {
            tr(color = getTitleColor()) {
                td {
                    text("Product")
                }
                td {
                    text("Price")
                }
                td {
                    text("Popularity")
                }
            }
            val products = getProductsData()
            for ((index, product) in products.withIndex()) {       // iterator che estrae elemento e indice corrispondente
                tr {
                    td(color = getCellColor(index, 0)) {
                        text(product.description)
                    }
                    td(color = getCellColor(index, 1)) {
                        text(product.price)
                    }
                    td(color = getCellColor(index, 2)) {
                        text(product.popularity)
                    }
                }
            }
        }
    }.toString()    // toString() definita nella classe Tag che viene richiamata da Html:Tag("html")
}

fun getTitleColor() = "#b9c9fe"
fun getCellColor(index: Int, column: Int) = if ((index + column) % 2 == 0) "#dce4ff" else "#eff2ff"


/* GENERICS e VARIANZA */
interface Source<out T> {
    fun nextT(): T
}
fun demo(strs: Source<String>) {
    val objects: Source<Any> = strs // Questo è finchè T è un parametro esterno a Strings (in questo caso)
    // ...
}

interface Comparable<in T> {
    operator fun compareTo(other: T): Int
}
fun demo(x: Comparable<Number>) {
    x.compareTo(1.0)            // Va bene perchè Double è sottotipo di Number
    val y: Comparable<Double> = x
}

fun copy(from: Array<out Any>, to: Array<Any>) {
    assert(from.size == to.size)
    for (i in from.indices)
        to[i] = from[i]
}


fun <T, C : MutableCollection<T>> Collection<T>.partitionTo(first: C, second: C, predicate: (T) -> Boolean): Pair<C, C> {
    for (element in this) {
        if (predicate(element)) {
            first.add(element)
        } else {
            second.add(element)
        }
    }
    return Pair(first, second)
}

fun partitionWordsAndLines() {
    val (words, lines) = listOf("a", "a b", "c", "d e")
        .partitionTo(ArrayList(), ArrayList()) { s -> !s.contains(" ") }
    check(words == listOf("a", "c"))
    check(lines == listOf("a b", "d e"))
}

fun partitionLettersAndOtherSymbols() {
    val (letters, other) = setOf('a', '%', 'r', '}')
        .partitionTo(HashSet(), HashSet()) { c -> c in 'a'..'z' || c in 'A'..'Z' }
    check(letters == setOf('a', 'r'))
    check(other == setOf('%', '}'))
}


fun main() {
//    main1()
//    main2()
//    main3()
//    main4()
//    main5()
//    main6()
    main7()
}














