package BildScript

import java.awt.image.BufferedImage
import java.io.File

import javax.imageio.ImageIO

class RasterImage(imageWidth: Int, imageHeight: Int) {

  val bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)

  def draw(d: Drawable, drawableWidth: Double): Unit = {
    println("starting draw")
    val pixelPerPoint = imageWidth / drawableWidth

    for (rowIndex <- 0 until imageHeight) {
      if (rowIndex % (imageHeight / 10) == 0)
        println("drawing row " + rowIndex)
      for (columnIndex <- 0 until imageWidth) {
        val pointToSample = Point(columnIndex / pixelPerPoint, rowIndex / pixelPerPoint)
        bufferedImage.setRGB(rowIndex, columnIndex, d.sample(pointToSample).toARGB)
      }
    }

    /*var rowIndex = -1
    var columnIndex = -1
    val newColorInfo = colorInformation.map { row =>
      rowIndex = rowIndex + 1
      if (rowIndex % (imageHeight / 10) == 0) println("drawing row " + rowIndex)
      val result = row.map { color =>
        columnIndex = columnIndex + 1
        val pointToSample = Point(columnIndex / pixelPerPoint, rowIndex / pixelPerPoint)
        val newColor = d.sample(pointToSample)
        color.overlay(newColor)
      }
      columnIndex = 0
      result
    }
    RasterImage(newColorInfo)*/
  }

  def output(fileName: String): Unit = {
    /*println("starting Output")
    val bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
    colorInformation.zipWithIndex.foreach { case (row, rowIndex) =>
      row.zipWithIndex.foreach { case (color, columnIndex) =>
        val jColor = new java.awt.Color(color.red, color.green, color.blue, color.alpha)
        bufferedImage.setRGB(columnIndex, rowIndex, jColor.getRGB)
      }
    }*/
    val outputfile = new File(fileName)
    ImageIO.write(bufferedImage, "png", outputfile)
  }

}

object RasterImage {

  def apply(res: Resolution): RasterImage = {
    new RasterImage(res.x, res.y)
  }

}