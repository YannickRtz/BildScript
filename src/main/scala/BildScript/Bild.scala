package BildScript

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

  def sample(p: Point): Color = {

    val afterTransform = transformations.foldLeft(p)((prev, transform) => transform.exec(prev))

    if (masks.nonEmpty && !masks.exists(_.test(afterTransform)))
      Color.CLEAR
    else {
      val colors = layers.map(_.sample(afterTransform))
      val result = colors.foldLeft(Color.CLEAR)(_.overlay(_))
      result
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