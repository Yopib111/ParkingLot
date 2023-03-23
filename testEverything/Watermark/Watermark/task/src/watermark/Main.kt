package watermark

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

class Image {
    var image: BufferedImage? = null
    object TransparencyColor{
        var transparencyColor = mutableListOf<Int>(-1, -1, -1)
    }


    fun getFirstImage(): BufferedImage {

        println("Input the image filename:")
        val filePath = readln()
        val imageFile = File(filePath)
        if (!imageFile.exists()) {
            println("The file $filePath doesn't exist.")
            exit()
        }
        val image = ImageIO.read(imageFile)
        if (image.colorModel.numColorComponents != 3) {
            println("The number of image color components isn't 3.")
            exit()
        }
        if (image.colorModel.pixelSize != 32 && image.colorModel.pixelSize != 24) {
            println("The image isn't 24 or 32-bit.")
            exit()
        }
        return image
    }

    fun getWatermarkImage(): BufferedImage {
        println("Input the watermark image filename:")
        val filePath = readln()
        val imageFile = File(filePath)
        if (!imageFile.exists()) {
            println("The file $filePath doesn't exist.")
            exit()
        }
        val image = ImageIO.read(imageFile)
        if (image.colorModel.numColorComponents != 3) {
            println("The number of watermark color components isn't 3.")
            exit()
        }
        if (image.colorModel.pixelSize != 32 && image.colorModel.pixelSize != 24) {
            println("The watermark isn't 24 or 32-bit.")
            exit()
        }
        return image

    }

    fun saveImage(newImage: BufferedImage, extension: String, outputFile: File) {
        ImageIO.write(newImage, extension, outputFile)
    }
    fun createNewImage(
        firstImage: BufferedImage,
        watermarkImage: BufferedImage,
        transparency: Int,
        alphaCh: String,
        positionMethod: String,
        watermarkPosition: MutableList<String>
        ): BufferedImage {
        val newImage = BufferedImage(firstImage.width, firstImage.height, BufferedImage.TYPE_INT_RGB)

        when (positionMethod) {

            "single" -> {
                var watermarkPositionX = watermarkPosition[0].toInt()
                var watermarkPositionY = watermarkPosition[1].toInt()
                var currentWatermarkPositionX = 0
                var currentWatermarkPositionY = 0

                for (x in 0 until firstImage.width) {
                    for (y in 0 until firstImage.height) {
                        val i = Color(firstImage.getRGB(x, y))
                        if (x == watermarkPositionX && y == watermarkPositionY) {
                            val w = if (alphaCh == "yes") Color(watermarkImage.getRGB(currentWatermarkPositionX, currentWatermarkPositionY), true)
                            else Color(watermarkImage.getRGB(currentWatermarkPositionX, currentWatermarkPositionY))

                            val color = Color(
                                (transparency * w.red + (100 - transparency) * i.red) / 100,
                                (transparency * w.green + (100 - transparency) * i.green) / 100,
                                (transparency * w.blue + (100 - transparency) * i.blue) / 100
                            )
                            if (w.alpha == 0) newImage.setRGB(x, y, Color(firstImage.getRGB(x, y)).rgb)
                            else if (w.red == TransparencyColor.transparencyColor[0] && w.green == TransparencyColor.transparencyColor[1]
                                && w.blue == TransparencyColor.transparencyColor[2]
                            ) {
                                newImage.setRGB(x, y, i.rgb)

                            } else newImage.setRGB(x, y, color.rgb)

                            if (y - watermarkPosition[1].toInt() == watermarkImage.height-1) {
                                watermarkPositionY = watermarkPosition[1].toInt()
                                currentWatermarkPositionY = 0
                                if (watermarkPositionX < watermarkPosition[0].toInt() + watermarkImage.width-1) {
                                    watermarkPositionX++
                                    currentWatermarkPositionX++
                                }
                            }
                            else {
                                watermarkPositionY++
                                currentWatermarkPositionY++
                            }
                        } else newImage.setRGB(x, y, i.rgb)
                    }
                }
            }

            "grid" -> {
                var currentWatermarkPositionX = 0
                var currentWatermarkPositionY = 0

                for (x in 0 until firstImage.width) {
                    for (y in 0 until firstImage.height) {
                        val i = Color(firstImage.getRGB(x, y))
                        val w = if (alphaCh == "yes") Color(watermarkImage.getRGB(currentWatermarkPositionX, currentWatermarkPositionY), true)
                            else Color(watermarkImage.getRGB(currentWatermarkPositionX, currentWatermarkPositionY))

                        val color = Color(
                            (transparency * w.red + (100 - transparency) * i.red) / 100,
                            (transparency * w.green + (100 - transparency) * i.green) / 100,
                            (transparency * w.blue + (100 - transparency) * i.blue) / 100
                        )
                        if (w.alpha == 0) newImage.setRGB(x, y, Color(firstImage.getRGB(x, y)).rgb)
                            else if (w.red == TransparencyColor.transparencyColor[0] && w.green == TransparencyColor.transparencyColor[1]
                                && w.blue == TransparencyColor.transparencyColor[2]
                            ) {
                                newImage.setRGB(x, y, i.rgb)
                            } else
                                newImage.setRGB(x, y, color.rgb)

                        if (currentWatermarkPositionY == watermarkImage.height-1) {
                            currentWatermarkPositionY = 0
                        } else {
                            currentWatermarkPositionY++
                        }
                        if (y == firstImage.height-1) {
                            currentWatermarkPositionX++
                        }
                        if (currentWatermarkPositionX > watermarkImage.width-1) {
                            currentWatermarkPositionX = 0
                        }
                    }
                }



            }
        }

        return  newImage
    }
}

fun main() {
    val firstImage = Image()
    firstImage.image = Image().getFirstImage()

    val watermarkImage = Image()
    watermarkImage.image = Image().getWatermarkImage()
    var alphaChannel = ""
    val transparencyColor: List<Int>

    if (watermarkImage.image?.transparency == 3) {
        println("Do you want to use the watermark's Alpha channel?")
        alphaChannel = readln().lowercase()
    } else {
        if (watermarkImage.image!!.width > firstImage.image!!.width || watermarkImage.image!!.height > firstImage.image!!.height) {
            println("The watermark's dimensions are larger.")
            exit()
        } else Image.TransparencyColor.transparencyColor = setTransporencyColor()
    }

    println("Input the watermark transparency percentage (Integer 0-100):")
    val transparency = readln()
    try {
        transparency.toInt()
    }
    catch (e: Exception) {
        println("The transparency percentage isn't an integer number.")
        exit()
    }
    if (transparency.toInt() !in 0..100) {
        println("The transparency percentage is out of range.")
        exit()
    }

    println("Choose the position method (single, grid):")
    var watermarkPosition = mutableListOf<String>("0","0")
    val positionMethod = readln()
    if (positionMethod != "single" && positionMethod != "grid") {
        println("The position method input is invalid.")
        exit()
    }
    when (positionMethod) {
        "single" -> {
            val diffX = firstImage.image!!.width - watermarkImage.image!!.width
            val diffY = firstImage.image!!.height - watermarkImage.image!!.height
            println("Input the watermark position ([x 0-$diffX] [y 0-$diffY]):")
            watermarkPosition = readln().split(' ').toMutableList()
            try {
                watermarkPosition[0].toInt()
                watermarkPosition[1].toInt()
            } catch (e: Exception) {
                println("The position input is invalid.")
                exit()
            }
            if (watermarkPosition[0].toInt() > firstImage.image!!.width - watermarkImage.image!!.width ||
                watermarkPosition[1].toInt() > firstImage.image!!.height - watermarkImage.image!!.height ||
                watermarkPosition[0].toInt() < 0 ||
                watermarkPosition[1].toInt() < 0 ){
                println("The position input is out of range.")
                exit()
            }
        }
    }


    println("Input the output image filename (jpg or png extension):")
    val outputFilename = readln()
    val outputFilenameExtension = outputFilename.substringAfter('.')
    if (outputFilenameExtension != "jpg" && outputFilenameExtension != "png") {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exit()
    }

    val outputFile = File(outputFilename)
    val newImage = Image()
    newImage.image = Image().createNewImage(firstImage.image!!,
        watermarkImage.image!!, transparency.toInt(), alphaChannel, positionMethod, watermarkPosition)
    Image().saveImage(newImage.image!!, outputFilenameExtension, outputFile)
    println("The watermarked image $outputFilename has been created.")


}

fun setTransporencyColor(): MutableList<Int> {
    println("Do you want to set a transparency color?")
    val answer = readln()
    val regex = Regex("\\d{0,3} \\d{0,3} \\d{0,3}")

    if (answer == "yes") {
        println("Input a transparency color ([Red] [Green] [Blue]):")
        val transparencyColor = readln()
        if (transparencyColor.matches(regex)) {
            val rColor = transparencyColor.substringBefore(' ').toInt()
            val newString = transparencyColor.substringAfter(' ')
            val gColor = newString.substringBefore(' ').toInt()
            val bColor = newString.substringAfter(' ').toInt()
            if (rColor in 0..255 && gColor in 0..255 && bColor in 0..255)
                return mutableListOf(rColor, gColor, bColor)
            else {
                println("The transparency color input is invalid.")
                exit()
            }

        } else {
            println("The transparency color input is invalid.")
            exit()
        }
    }
    return mutableListOf(-1, -1, -1)

}


//fun getImageInfo(image: BufferedImage) {
//    val imageWidth = image.width
//    val imageHeight = image.height
//    val imageNumberOfComponents = image.colorModel.numComponents
//    val imageNumberOfColorComponents = image.colorModel.numColorComponents
//    val imageBitsOnPix = image.colorModel.pixelSize
//    val imageTransparency = image.transparency
//
//    println("Width: $imageWidth\nHeight: $imageHeight\nNumber of components: $imageNumberOfComponents\n" +
//            "Number of color components: $imageNumberOfColorComponents\nBits per pixel: $imageBitsOnPix")
//
//    when (imageTransparency) {
//        1 -> println("Transparency: OPAQUE")
//        2 -> println("Transparency: BITMASK")
//        3 -> println("Transparency: TRANSLUCENT")
//    }
//}


fun exit(){
    exitProcess(0)
}
