fun countStrings(list: List<Any>): Int {
    var count = 0
    for (i in list) {
        if (i is String) count++
    }
    return count
    // make your code here
}