package BildPackage

import java.awt.image.BufferedImage
import java.io.File

import BildPackage.Masks.RectMask
import BildPackage.Transformations.Translation
import javax.imageio.ImageIO

import scala.annotation.tailrec
import scala.collection.mutable

class Bild(masks: Seq[Mask], fillings: Seq[Filling], transformations: Seq[Transformation], bilder: Seq[Bild]) extends Addable {

  override def next: Bild = {
    val newMasks = masks.map(_.next)
    val newTransformations = transformations.map(_.next)
    val newFillings = fillings.map(_.next)
    val newBilder = bilder.map(_.next)
    new Bild(newMasks, newFillings, newTransformations, newBilder)
  }

  override def walk(tc: Seq[Int]): Unit = {
    masks.foreach(_.walk(tc))
    fillings.foreach(_.walk(tc))
    transformations.foreach(_.walk(tc))
    val newTC: mutable.Buffer[Int] = mutable.Buffer()
    tc.copyToBuffer(newTC)
    newTC += 0
    bilder.foreach { b: Bild =>
      b.walk(newTC)
      newTC.update(newTC.length - 1, newTC.last + 1)
    }
  }

  def add(a: Addable): Bild = a match {
    case m: Mask => new Bild(masks :+ m, fillings, transformations, bilder)
    case f: Filling => new Bild(masks, fillings :+ f, transformations, bilder)
    case t: Transformation => new Bild(masks, fillings, transformations :+ t, bilder)
    case b: Bild => new Bild(masks, fillings, transformations, bilder :+ b)
  }

  @tailrec
  final def add(list: Seq[Addable]): Bild = {
    if (list.isEmpty) this
    else if (list.size < 2) add(list.head)
    else add(list.head).add(list.tail)
  }

  def draw(canvas: BufferedImage, pixelPerPoint: Double, prevTransformations: Seq[Transformation],
           doAntiAliasing: Boolean, useBBox: Boolean, visualizeBBox: Boolean): Unit = {
    val allTransformations = prevTransformations ++ transformations
    if (fillings.nonEmpty) {
      val newTransformations: mutable.Buffer[Transformation] = mutable.Buffer()
      val prevNonlocalTransformations: mutable.Buffer[Transformation] = mutable.Buffer()
      val allNonlocalTransformations = allTransformations.filter {
        case _: LocalTransform => false
        case _ => true
      }
      newTransformations ++= allNonlocalTransformations
      allTransformations.reverse.foreach {
        case l: LocalTransform =>
          val newPivot = l.pivotPoint.applyTransformsReverse(prevNonlocalTransformations)
          val vector = newPivot.applyTransforms(allNonlocalTransformations)
          newTransformations += Translation(-1 * vector.x, -1 * vector.y)
          newTransformations += l
          newTransformations += Translation(vector.x, vector.y)

        case t: Transformation =>
          prevNonlocalTransformations += t
      }

      // Enable drawing when no mask is set:
      val defaultMask =
        if (masks.isEmpty) Seq(RectMask(canvas.getWidth / pixelPerPoint, canvas.getHeight / pixelPerPoint))
        else Seq()
      (defaultMask ++ masks).foreach { m =>
        // Values for when we're not using the bounding box:
        var maxX = canvas.getWidth
        var maxY = canvas.getHeight
        var minX = 0
        var minY = 0

        if (useBBox) {
          val topLeftBBPoint = Point(0,0).applyTransforms(newTransformations)
          val bottomRightBBPoint = m.boundingBoxDimensions.applyTransforms(newTransformations)
          val topRightBBPoint = Point(m.boundingBoxDimensions.x, 0).applyTransforms(newTransformations)
          val bottomLeftBBPoint = Point(0, m.boundingBoxDimensions.y).applyTransforms(newTransformations)
          val BBPoints = Seq(topLeftBBPoint, bottomRightBBPoint, topRightBBPoint, bottomLeftBBPoint)
          // Mutation!
          minX = Math.max(0, Math.round(BBPoints.minBy(_.x).x * pixelPerPoint).toInt)
          minY = Math.max(0, Math.round(BBPoints.minBy(_.y).y * pixelPerPoint).toInt)
          maxX = Math.min(canvas.getWidth, Math.round(BBPoints.maxBy(_.x).x * pixelPerPoint).toInt)
          maxY = Math.min(canvas.getHeight, Math.round(BBPoints.maxBy(_.y).y * pixelPerPoint).toInt)
        }

        for (y <- minY until maxY) {
          for (x <- minX until maxX) {
            if (doAntiAliasing) {
              val subPixelColors = for (d <- Bild.subPixelDeltas) yield {
                val withoutTransform = Point((x + d.x) / pixelPerPoint, (y + d.y) / pixelPerPoint)
                  .applyTransformsReverse(newTransformations)
                if (m.test(withoutTransform)) {
                  val subColor = Color.CLEAR
                  fillings.foreach { f =>
                    val fillingColor = f.trace(withoutTransform)
                    subColor.overlayMutate(fillingColor)
                  }
                  subColor
                } else {
                  if (visualizeBBox) Color.RED
                  else Color.CLEAR  // TODO: Optimize by filtering these?
                }
              }
              val averageColor = averageColors(subPixelColors)
              val finalColor = Color.fromARGB(canvas.getRGB(x, y))
              finalColor.overlayMutate(averageColor)
              canvas.setRGB(x, y, finalColor.toARGB)

            } else {  // No anti aliasing:

              val withoutTransform = Point(x / pixelPerPoint,y / pixelPerPoint).applyTransformsReverse(newTransformations)
              if (m.test(withoutTransform)) {
                val canvasColor = Color.fromARGB(canvas.getRGB(x, y))
                fillings.foreach { f =>
                  val fillingColor = f.trace(withoutTransform)
                  canvasColor.overlayMutate(fillingColor)
                }
                canvas.setRGB(x, y, canvasColor.toARGB)
              } else {
                if (visualizeBBox)
                  canvas.setRGB(x, y, Color.RED.toARGB)
              }
            }
          }
        }
      }
    }

    def averageColors(colors: Seq[Color]): Color = {
      var redSum = 0.0
      var greenSum = 0.0
      var blueSum = 0.0
      var alphaSum = 0.0
      colors.foreach(c => {
        redSum += c.red * c.alpha
        greenSum += c.green * c.alpha
        blueSum += c.blue * c.alpha
        alphaSum += c.alpha
      })
      if (alphaSum == 0) Color.CLEAR
      else Color(redSum / alphaSum, greenSum / alphaSum, blueSum / alphaSum, alphaSum / colors.length)
    }

    bilder.foreach(_.draw(canvas, pixelPerPoint, allTransformations, doAntiAliasing, useBBox, visualizeBBox))
  }

  def raster(resolutionX: Int, resolutionY: Int, width: Double, fileName: String, doAntiAliasing: Boolean,
             useBBox: Boolean, visualizeBBox: Boolean): Unit = {
    println("Executing walk...")
    walk(Seq(0))
    val bufferedImage = new BufferedImage(resolutionX, resolutionY, BufferedImage.TYPE_INT_ARGB)
    val pixelPerPoint = resolutionX.toDouble / width
    println("Rasterizing...")
    draw(bufferedImage, pixelPerPoint, Seq(), doAntiAliasing, useBBox, visualizeBBox)
    println("File output...")
    val outputfile = new File(fileName)
    ImageIO.write(bufferedImage, "png", outputfile)
    println("done")
  }

}

object Bild {
  val subPixelDeltas = Seq(
    Point(-0.3, -0.3), Point(0.3, -0.3), Point(-0.3, 0.3), Point(0.3, 0.3)
  )
  def apply(): Bild = new Bild(Seq(), Seq(), Seq(), Seq())
  def apply(l: Seq[Addable]): Bild = apply().add(l)
  def apply(a: Addable): Bild = apply().add(a)
}