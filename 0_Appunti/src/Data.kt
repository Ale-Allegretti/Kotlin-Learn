
data class ProductData(val description: String, val price: Double, val popularity: Int)

val cactus = ProductData("cactus", 11.2, 13)
val cake = ProductData("cake", 3.2, 111)
val camera = ProductData("camera", 134.5, 2)
val car = ProductData("car", 30000.0, 0)
val carrot = ProductData("carrot", 1.34, 5)
val cellPhone = ProductData("cell phone", 129.9, 99)
val chimney = ProductData("chimney", 190.0, 2)
val certificate = ProductData("certificate", 99.9, 1)
val cigar = ProductData("cigar", 8.0, 51)
val coffee = ProductData("coffee", 8.0, 67)
val coffeeMaker = ProductData("coffee maker", 201.2, 1)
val cola = ProductData("cola", 4.0, 67)
val cranberry = ProductData("cranberry", 4.1, 39)
val crocs = ProductData("crocs", 18.7, 10)
val crocodile = ProductData("crocodile", 20000.2, 1)
val cushion = ProductData("cushion", 131.0, 0)

fun getProductsData() = listOf(cactus, cake, camera, car, carrot, cellPhone, chimney, certificate, cigar, coffee, coffeeMaker,
        cola, cranberry, crocs, crocodile, cushion)
