package BildScript

import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

class RasterImage(imageWidth: Int, imageHeight: Int) {

  val bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)

  def draw(d: Drawable, drawableWidth: Float): Unit = {
    println("starting draw")

    val pixelPerPoint = imageWidth / drawableWidth
    val pointToSample = Point(0, 0)

    for (rowIndex <- 0 until imageHeight) {
      if (rowIndex % (imageHeight / 10) == 0)
        println("drawing row " + rowIndex)
      for (columnIndex <- 0 until imageWidth) {
        pointToSample.x = columnIndex / pixelPerPoint
        pointToSample.y = rowIndex / pixelPerPoint
        bufferedImage.setRGB(rowIndex, columnIndex, d.sample(pointToSample))
      }
    }
  }

  def output(fileName: String): Unit = {
    val outputfile = new File(fileName)
    ImageIO.write(bufferedImage, "png", outputfile)
  }

}

object RasterImage {

  def apply(res: Resolution): RasterImage = {
    new RasterImage(res.x, res.y)
  }

}