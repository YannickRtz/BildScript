package WatchMe
import BildScript.Fillings.SolidSurface
import BildScript.Generators.EvoGen
import BildScript.Masks.RectMask
import BildScript.Transformations.{PositionTransform, RotationTransform}
import BildScript.{Bild, Resolution, _}

import scala.language.postfixOps
object TryOutZone extends App {

  val rectWidth = 15
  val picWidth = 500
  val padding = 50
  val numRects = 300
  val halfRect = rectWidth / 2
  val halfPic = picWidth / 2
  val centerOffset = halfPic - halfRect + 50

  Bild {
    SolidSurface(GColor("111111")) +
    Bild {
      SolidSurface(GColor("aaffaa")) +
      RectMask(rectWidth, rectWidth) +
      PositionTransform(
        EvoGen(_ * -1 * 0.002 * picWidth + centerOffset),
        EvoGen(_ * -1 * 0.002 * picWidth + centerOffset)
      ) +
      RotationTransform(
        EvoGen(_ * (360 / (numRects / 10))),
        EvoGen(_ * 0.002 * picWidth),
        EvoGen(_ * 0.002 * picWidth)
      ) +
    } * numRects
  }

    .raster(Resolution(picWidth * 2, picWidth * 2), picWidth + 100)
    .output("image.png")
}
