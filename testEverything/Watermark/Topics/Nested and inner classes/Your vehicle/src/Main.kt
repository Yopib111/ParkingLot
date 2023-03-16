class Vehicle {
    inner class Engine {
        fun start() {
            println("RRRrrrrrrr....")
        }
    }
}
// do not touch the class above

fun main() {
    val start = Vehicle()
    start.Engine().start()
    // start your vehicle, put your code here
}