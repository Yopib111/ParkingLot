import java.awt.Color
import java.awt.image.BufferedImage

fun drawLines(): BufferedImage {
    val image = BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.color = Color.RED
    graphics.drawLine(0, 0, 200, 200)
    graphics.color = Color.GREEN
    graphics.drawLine(200, 0, 0, 200)
    return image
    // Add your code here
}
