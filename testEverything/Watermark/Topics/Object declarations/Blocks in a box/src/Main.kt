//class Block(val color: String) {
//    object BlockProperties {
//        var length = 4
//        var width = 6
//
//        val aaa = setOf(1, 2, 3)
//
//        fun blocksInBox(lengthBox: Int, widthBox: Int) = (lengthBox/ length) *(widthBox/ width)
//
//    }
//}


fun main() {
        val array = ArrayList<Int>(3)
        val arrayQQQ = ArrayList<Int>(3)

        array.removeAll(arrayQQQ)
        array.retainAll(arrayQQQ)

}