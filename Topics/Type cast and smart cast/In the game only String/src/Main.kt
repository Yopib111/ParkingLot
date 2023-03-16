fun <T> getStringsOnly(list: List<T>): List<String> {
    val result = mutableListOf<String>()
    for (i in list) {
        if (i is String) {
            result.add(i)
        }
    }
    // make your code here
    return result
}