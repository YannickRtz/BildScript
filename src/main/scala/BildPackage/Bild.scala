package BildPackage

import java.awt.image.BufferedImage
import java.io.File

import BildPackage.Masks.RectMask
import BildPackage.Transformations.Translation
import javax.imageio.ImageIO

import scala.annotation.tailrec
import scala.collection.mutable

class Bild(masks: Seq[Mask], fillings: Seq[Filling], transformations: Seq[Transformation], bilder: Seq[Bild]) extends Addable {

  def next: Bild = {
    val newMasks = masks.map(_.next)
    val newTransformations = transformations.map(_.next)
    val newFillings = fillings.map(_.next)
    val newBilder = bilder.map(_.next)
    new Bild(newMasks, newFillings, newTransformations, newBilder)
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

  def draw(canvas: BufferedImage, pixelPerPoint: Double, prevTransformations: Seq[Transformation], tc: Seq[Int]): Unit = {
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
          val newPivot = l.pivotPoint(tc).applyTransformsReverse(prevNonlocalTransformations, tc)
          val vector = newPivot.applyTransforms(allNonlocalTransformations, tc)
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
        // Use bounding box:
        val topLeftBBPoint = Point(0,0).applyTransforms(newTransformations, tc)
        val bottomRightBBPoint = m.boundingBoxDimensions(tc).applyTransforms(newTransformations, tc)
        val topRightBBPoint = Point(m.boundingBoxDimensions(tc).x, 0).applyTransforms(newTransformations, tc)
        val bottomLeftBBPoint = Point(0, m.boundingBoxDimensions(tc).y).applyTransforms(newTransformations, tc)
        val BBPoints = Seq(topLeftBBPoint, bottomRightBBPoint, topRightBBPoint, bottomLeftBBPoint)
        val minX = Math.max(0, Math.round(BBPoints.minBy(_.x).x * pixelPerPoint).toInt)
        val minY = Math.max(0, Math.round(BBPoints.minBy(_.y).y * pixelPerPoint).toInt)
        val maxX = Math.min(canvas.getWidth, Math.round(BBPoints.maxBy(_.x).x * pixelPerPoint).toInt)
        val maxY = Math.min(canvas.getHeight, Math.round(BBPoints.maxBy(_.y).y * pixelPerPoint).toInt)
        // To skip bounding box:
        /*val maxX = canvas.getWidth
        val maxY = canvas.getHeight
        val minX = 0
        val minY = 0*/

        for (y <- minY until maxY) {
          for (x <- minX until maxX) {
            val withoutTransform = Point(x / pixelPerPoint,y / pixelPerPoint).applyTransformsReverse(newTransformations, tc)
            if (m.test(withoutTransform, tc)) {
              val canvasColor = Color.fromARGB(canvas.getRGB(x, y))
              fillings.foreach { f =>
                val fillingColor = f.trace(withoutTransform, tc)
                canvasColor.overlayMutate(fillingColor)
              }
              canvas.setRGB(x, y, canvasColor.toARGB)
            } else {
              // Debug visualization of bounding boxes:
              // canvas.setRGB(x, y, Color.RED.toARGB)
            }
          }
        }
      }
    }

    val newTC: mutable.Buffer[Int] = mutable.Buffer()
    tc.copyToBuffer(newTC)
    newTC += 0
    bilder.foreach { b: Bild =>
      println(newTC + "NEXT")
      b.draw(canvas, pixelPerPoint, allTransformations, newTC)
      newTC.update(newTC.length - 1, newTC.last + 1)
    }
  }

  def raster(resolutionX: Int, resolutionY: Int, width: Double, fileName: String): Unit = {
    val bufferedImage = new BufferedImage(resolutionX, resolutionY, BufferedImage.TYPE_INT_ARGB)
    val pixelPerPoint = resolutionX.toDouble / width
    println("Rasterizing...")
    draw(bufferedImage, pixelPerPoint, Seq(), Seq(0))
    println("File output...")
    val outputfile = new File(fileName)
    ImageIO.write(bufferedImage, "png", outputfile)
    println("done")
  }

}

object Bild {
  def apply(): Bild = new Bild(Seq(), Seq(), Seq(), Seq())
  def apply(l: Seq[Addable]): Bild = apply().add(l)
  def apply(a: Addable): Bild = apply().add(a)
}