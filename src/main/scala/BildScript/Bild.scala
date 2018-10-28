package BildScript

import java.awt.image.BufferedImage

import BildScript.Masks.RectMask
import BildScript.Transformations.PositionTransform

import scala.annotation.tailrec
import scala.collection.mutable

class Bild(masks: Seq[Mask], fillings: Seq[Filling], transformations: Seq[Transformation], bilder: Seq[Bild]) extends Addable {
  // println("new bild")

  def next: Bild = {
    val newMasks = masks.map(_.next)
    val newTransformations = transformations.map(_.next)
    val newFillings = fillings.map(_.next)
    new Bild(newMasks, newFillings, newTransformations, bilder)
  }

  def trace(p: Point): Color = {

    val afterTransform = transformations.foldRight(p)((transform, prev) => transform.execReverse(prev))

    if (masks.nonEmpty && !masks.exists(_.test(afterTransform)))
      Color.CLEAR
    else {
      val colorsBilder = bilder.map(_.trace(afterTransform))
      val colorsFillings = fillings.map(_.trace(afterTransform))
      val result = (colorsFillings ++ colorsBilder).foldLeft(Color.CLEAR)(_.overlay(_))
      result
    }
  }

  def draw(canvas: BufferedImage, pixelPerPoint: Double, prevTransformations: Seq[Transformation]): Unit = {
    val allTransformations: mutable.Buffer[Transformation] = mutable.Buffer()
    val newTransformations: mutable.Buffer[Transformation] = mutable.Buffer()
    val prevNonlocalTransformations: mutable.Buffer[Transformation] = mutable.Buffer()
    allTransformations ++= prevTransformations ++= transformations
    val allNonlocalTransformations = allTransformations.filter {
      case _: LocalTransform => false
      case _ => true
    }
    newTransformations ++= allNonlocalTransformations
    allTransformations.reverse.foreach {
      case l: LocalTransform =>
        val newPivot = prevNonlocalTransformations.foldRight(l.pivotPoint)((t, prev) => t.execReverse(prev))
        val vector = allNonlocalTransformations.foldLeft(newPivot)((prev, t) => t.exec(prev))
        newTransformations += PositionTransform(-1 * vector.x, -1 * vector.y)
        newTransformations += l
        newTransformations += PositionTransform(vector.x, vector.y)

      case t: Transformation =>
        prevNonlocalTransformations += t
    }

    // Enable drawing when no mask is set:
    val defaultMask =
      if (masks.isEmpty) Seq(RectMask(canvas.getWidth / pixelPerPoint, canvas.getHeight / pixelPerPoint))
      else Seq()
    (defaultMask ++ masks).foreach { m =>
      val topLeftBBPoint = newTransformations.foldLeft(Point(0,0))((prev, t) => t.exec(prev))
      val bottomRightBBPoint = newTransformations.foldLeft(m.boundingBoxDimensions)((prev, t) => t.exec(prev))
      val topRightBBPoint = newTransformations.foldLeft(Point(m.boundingBoxDimensions.x, 0))((prev, t) => t.exec(prev))
      val bottomLeftBBPoint = newTransformations.foldLeft(Point(0, m.boundingBoxDimensions.y))((prev, t) => t.exec(prev))
      val BBPoints = Seq(topLeftBBPoint, bottomRightBBPoint, topRightBBPoint, bottomLeftBBPoint)
      val minWidth = Math.max(0, Math.round(BBPoints.minBy(_.x).x * pixelPerPoint).toInt)
      val minHeight = Math.max(0, Math.round(BBPoints.minBy(_.y).y * pixelPerPoint).toInt)
      val maxWidth = Math.min(canvas.getWidth, Math.round(BBPoints.maxBy(_.x).x * pixelPerPoint).toInt)
      val maxHeight = Math.min(canvas.getHeight, Math.round(BBPoints.maxBy(_.y).y * pixelPerPoint).toInt)
      // To skip bounding box:
      /*val maxWidth = canvas.getWidth
      val maxHeight = canvas.getHeight
      val minWidth = 0
      val minHeight = 0*/
      if (fillings.nonEmpty) {
        for (y <- minHeight until maxHeight) {
          for (x <- minWidth until maxWidth) {
            // val afterTransform = applyTranformations(allTransformations, Point(x / pixelPerPoint,y / pixelPerPoint))
            //if (allTransformations.size > 4)
            //println("halt")
            val withoutTransform = newTransformations.foldRight(Point(x / pixelPerPoint,y / pixelPerPoint))((transform, prev) => transform.execReverse(prev))
            if (m.test(withoutTransform)) fillings.foreach { f =>
              canvas.setRGB(x, y, f.trace(withoutTransform).toARGB)
            }
          }
        }
      }
    }

    bilder.foreach {
      case b: Bild => b.draw(canvas, pixelPerPoint, allTransformations)
      case _ => Unit
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

  def raster(resolution: Resolution, width: Double): RasterImage = {
    println("Rasterization...")
    val result = RasterImage(resolution)
    result.draw(this, width)
    result
  }

  def raytrace(resolution: Resolution, width: Double): RasterImage = {
    println("Ray tracing...")
    val result = RasterImage(resolution)
    result.trace(this, width)
    result
  }

}

object Bild {
  def apply(): Bild = new Bild(Seq(), Seq(), Seq(), Seq())
  def apply(l: Seq[Addable]): Bild = apply().add(l)
  def apply(a: Addable): Bild = apply().add(a)
}