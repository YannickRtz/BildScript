package BildScript

import java.awt.image.BufferedImage

import scala.annotation.tailrec

class Bild(masks: Seq[Mask], layers: Seq[Drawable], transformations: Seq[Transformation]) extends Drawable {

  def next: Bild = {
    val newMasks = masks.map(_.next)
    val newTransformations = transformations.map(_.next)
    val newLayers = layers.map {
      case l: Filling => l.next
      case b: Bild => b
    }
    new Bild(newMasks, newLayers, newTransformations)
  }

  def trace(p: Point): Color = {

    val afterTransform = transformations.foldLeft(p)((prev, transform) => transform.exec(prev))

    if (masks.nonEmpty && !masks.exists(_.test(afterTransform)))
      Color.CLEAR
    else {
      val colors = layers.map(_.trace(afterTransform))
      val result = colors.foldLeft(Color.CLEAR)(_.overlay(_))
      result
    }
  }

  def draw(canvas: BufferedImage, pixelPerPoint: Double): Unit = {
    val maxWidth = canvas.getWidth
    val maxHeight = canvas.getHeight
    val minWidth = 0
    val minHeight = 0
    // Todo: Use bounding box
    for (y <- minHeight until maxHeight) {
      for (x <- minWidth until maxWidth) {
        val afterTransform = transformations.foldLeft(Point(x / pixelPerPoint,y / pixelPerPoint))((prev, transform) => transform.exec(prev))
        if (masks.isEmpty || masks.exists(_.test(afterTransform)))
          layers.foreach {
            case f: Filling => canvas.setRGB(x, y, f.trace(afterTransform).toARGB)
            case _ => Unit
          }
      }
    }

    layers.foreach {
      case b: Bild => b.draw(canvas, pixelPerPoint)
      case _ => Unit
    }
  }

  def add(a: Addable): Bild = a match {
    case m: Mask => new Bild(masks :+ m, layers, transformations)
    case d: Drawable => new Bild(masks, layers :+ d, transformations)
    case t: Transformation => new Bild(masks, layers, transformations :+ t)
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
  def apply(): Bild = new Bild(Seq(), Seq(), Seq())
  def apply(l: Seq[Addable]): Bild = apply().add(l)
  def apply(a: Addable): Bild = apply().add(a)
}