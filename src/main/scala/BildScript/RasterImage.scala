package BildScript

import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

class RasterImage(colorInformation: Seq[Seq[Color]]) {

  val imageHeight: Int = colorInformation.size
  val imageWidth: Int = colorInformation.head.size

  def draw(d: Drawable, drawableWidth: Double): RasterImage = {
    val pixelPerPoint = imageWidth / drawableWidth
    val newColorInfo = colorInformation.zipWithIndex.map { case (row, rowIndex) =>
      row.zipWithIndex.map { case (color, columnIndex) =>
        val pointToSample = Point(columnIndex / pixelPerPoint, rowIndex / pixelPerPoint)
        val newColor = d.sample(pointToSample)
        color.overlay(newColor)
      }
    }
    RasterImage(newColorInfo)
  }

  def output(fileName: String): Unit = {
    val bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
    colorInformation.zipWithIndex.foreach { case (row, rowIndex) =>
      row.zipWithIndex.foreach { case (color, columnIndex) =>
        val jColor = new java.awt.Color(color.red, color.green, color.blue, color.alpha)
        bufferedImage.setRGB(columnIndex, rowIndex, jColor.getRGB)
      }
    }
    val outputfile = new File(fileName)
    ImageIO.write(bufferedImage, "png", outputfile)
  }

}

object RasterImage {

  def apply(res: Resolution): RasterImage = {
    val emptyColorInfo = Seq.fill(res.y)(Seq.fill(res.x)(Color.CLEAR))
    new RasterImage(emptyColorInfo)
  }

  def apply(colorInformation: Seq[Seq[Color]]): RasterImage =
    new RasterImage(colorInformation)

}