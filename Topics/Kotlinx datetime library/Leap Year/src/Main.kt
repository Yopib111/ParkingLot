import kotlinx.datetime.*

fun isLeapYear(year: String): Boolean {
    try {
        val instant = Instant.parse(year + "-02-29T00:00:00Z")
        return true
    } catch (e: Exception) {
        return false
    }
    // Write your code here

    //
}

fun main() {
    val year = readln()
    println(isLeapYear(year))
}