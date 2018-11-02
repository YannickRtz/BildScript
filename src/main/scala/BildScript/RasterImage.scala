package BildScript

import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

class RasterImage(imageWidth: Int, imageHeight: Int) {
  // println("new rasterimage")

  // TODO: It does not seem like this class is still really needed

  val bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)

  def trace(b: Bild, drawableWidth: Double): Unit = {
    println("starting trace")
    val pixelPerPoint = imageWidth / drawableWidth

    for (rowIndex <- 0 until imageHeight) {
      if (rowIndex % (imageHeight / 10) == 0)
        println("drawing row " + rowIndex)
      for (columnIndex <- 0 until imageWidth) {
        val pointToSample = Point(columnIndex / pixelPerPoint, rowIndex / pixelPerPoint)
        bufferedImage.setRGB(columnIndex, rowIndex, b.trace(pointToSample).toARGB)
      }
    }
  }

  def draw(d: Bild, drawableWidth: Double): Unit = {
    val pixelPerPoint = imageWidth / drawableWidth
    d.draw(bufferedImage, pixelPerPoint, Seq())
  }

  def output(fileName: String): Unit = {
    println("File output...")
    val outputfile = new File(fileName)
    ImageIO.write(bufferedImage, "png", outputfile)
    println("done")
  }

}

object RasterImage {

  def apply(res: Resolution): RasterImage = {
    new RasterImage(res.x, res.y)
  }

}