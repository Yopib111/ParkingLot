import java.awt.Color
import java.awt.image.BufferedImage

fun drawPolygon(): BufferedImage {
    val image = BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.color = Color.YELLOW
    val x = intArrayOf(50, 100, 200, 250, 200, 100)
    val y = intArrayOf(150,250, 250, 150, 50, 50)
    graphics.drawPolygon(x, y, 6)
  // Add your code here
    return image
}