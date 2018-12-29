package WatchMe
import BildPackage.Fillings.SolidSurface
import BildPackage.Generators.{FromIndex, SimpleRnd}
import BildPackage.Masks.{CircMask, RectMask}
import BildPackage.Transformations.{Rotation, Translation}
import BildPackage.{Bild, _}
import Meta.Measurement

import scala.language.postfixOps

object TryOutZone extends App {

  val rectWidth = 12
  val picWidth = 500
  val padding = 50
  val numRects = 300
  val halfRect = rectWidth / 2
  val halfPic = picWidth / 2
  val centerOffset = halfPic - halfRect + 50

  BildScript(
    resolutionX = 1000,
    resolutionY = 1000,
    width = picWidth + 100,
    fileName = "image.png",
    doAntiAliasing = true
  )(
    SolidSurface(HEX("111111")) +
    numRects * Bild (
      SolidSurface(
        HSV(SimpleRnd(_ * 100 % 360, gaussian = true), 1, 1)
      ) +
      CircMask(rectWidth, x => 1 - x, 0.2) +
      Rotation(
        FromIndex(_ * (360 / (numRects / 10))),
        FromIndex(_ * 0.002 * picWidth + halfRect),
        FromIndex(_ * 0.002 * picWidth + halfRect)
      ) +
      Translation(
        FromIndex(_ * -1 * 0.002 * picWidth + centerOffset),
        FromIndex(_ * -1 * 0.002 * picWidth + centerOffset)
      )
    )
  )

}
