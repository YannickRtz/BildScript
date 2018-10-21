package BildScript

import java.awt.image.BufferedImage

import BildScript.Masks.RectMask

import scala.annotation.tailrec

class Bild(masks: Seq[Mask], fillings: Seq[Filling], transformations: Seq[Transformation], bilder: Seq[Bild]) extends Addable {
  println("new bild")

  def next: Bild = {
    val newMasks = masks.map(_.next)
    val newTransformations = transformations.map(_.next)
    val newFillings = fillings.map(_.next)
    new Bild(newMasks, newFillings, newTransformations, bilder)
  }

  def trace(p: Point): Color = {

    val afterTransform = transformations.foldLeft(p)((prev, transform) => transform.exec(prev))

    if (masks.nonEmpty && !masks.exists(_.test(afterTransform)))
      Color.CLEAR
    else {
      val colorsBilder = bilder.map(_.trace(afterTransform))
      val colorsFillings = fillings.map(_.trace(afterTransform))
      val result = (colorsBilder ++ colorsFillings).foldLeft(Color.CLEAR)(_.overlay(_))
      result
    }
  }

  def draw(canvas: BufferedImage, pixelPerPoint: Double, prevTransformations: Seq[Transformation]): Unit = {
    val allTransformations = prevTransformations ++ transformations
    // Enable drawing when no mask is set:
    val defaultMask =
      if (masks.isEmpty) Seq(RectMask(canvas.getWidth / pixelPerPoint, canvas.getHeight / pixelPerPoint))
      else Seq()
    (defaultMask ++ masks).foreach { m =>
      val topLeftPoint = allTransformations.foldLeft(Point(0,0))((prev, t) => t.exec(prev))
      val bottomRightPoint = allTransformations.foldLeft(m.boundingBoxDimensions)((prev, t) => t.exec(prev))
      val minWidth = Math.max(0, Math.round(topLeftPoint.x * pixelPerPoint).toInt)
      val minHeight = Math.max(0, Math.round(topLeftPoint.y * pixelPerPoint).toInt)
      val maxWidth = Math.min(canvas.getWidth, Math.round(bottomRightPoint.x * pixelPerPoint).toInt)
      val maxHeight = Math.min(canvas.getHeight, Math.round(bottomRightPoint.y * pixelPerPoint).toInt)
      if (fillings.nonEmpty) {
        for (y <- minHeight until maxHeight) {
          for (x <- minWidth until maxWidth) {
            // val afterTransform = applyTranformations(allTransformations, Point(x / pixelPerPoint,y / pixelPerPoint))
            val withoutTransform = Point((x / pixelPerPoint) - topLeftPoint.x, (y / pixelPerPoint) - topLeftPoint.y)
            if (m.test(withoutTransform)) fillings.foreach { f =>
              canvas.setRGB(x, y, f.trace(withoutTransform).toARGB)
            }
          }
        }
      }
    }

/*    val maxWidth = canvas.getWidth
    val maxHeight = canvas.getHeight
    val minWidth = 0
    val minHeight = 0
    for (y <- minHeight until maxHeight) {
      for (x <- minWidth until maxWidth) {
        val afterTransform = allTransformations.foldLeft(Point(x / pixelPerPoint,y / pixelPerPoint))((prev, transform) => transform.exec(prev))
        if (masks.isEmpty || masks.exists(_.test(afterTransform)))
          layers.foreach {
            case f: Filling => canvas.setRGB(x, y, f.trace(afterTransform).toARGB)
            case _ => Unit
          }
      }
    }*/

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
    val result = RasterImage(resolution)
    result.draw(this, width)
    result
  }

}

object Bild {
  def apply(): Bild = new Bild(Seq(), Seq(), Seq(), Seq())
  def apply(l: Seq[Addable]): Bild = apply().add(l)
  def apply(a: Addable): Bild = apply().add(a)
}