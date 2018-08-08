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
      /*var keepSampling = true
      var c1 = Color.CLEAR
      var layerIdx = layers.size - 1
      while(keepSampling && layerIdx > 0) {
        val c0 = layers(layerIdx).sample(afterTransform)
        c1 = c0.overlay(c1)
        keepSampling = c1.alpha < 1
        layerIdx = layerIdx - 1
      }
      c1*/
      val colors = layers.map(_.sample(afterTransform))
      colors.foldLeft(Color.CLEAR)(_.overlay(_))
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

  def raster(resolution: Resolution, width: Float): RasterImage =
    RasterImage(resolution).draw(this, width)

}

object Bild {
  def apply(): Bild = new Bild(Seq(), Seq(), Seq())
  def apply(l: Seq[Addable]): Bild = apply().add(l)
  def apply(a: Addable): Bild = apply().add(a)
}