package WatchMe
import BildPackage.Fillings.SolidSurface
import BildPackage.Generators.EvoGen
import BildPackage.Masks.RectMask
import BildPackage.Transformations.{Translation, Rotation}
import BildPackage.{Bild, _}

import scala.language.postfixOps

object TryOutZone extends App {

  val rectWidth = 15
  val picWidth = 500
  val padding = 50
  val numRects = 300
  val halfRect = rectWidth / 2
  val halfPic = picWidth / 2
  val centerOffset = halfPic - halfRect + 50

  BildScript(picWidth * 2, picWidth * 2, picWidth + 100, "image.png") (

    SolidSurface(HEX("111111")) +

    numRects * Bild (

      SolidSurface(
        HSV(EvoGen(_ * (360 / (numRects / 10)) % 360), 1, 1)
      ) +

      RectMask(rectWidth, rectWidth) +

      Translation(
        EvoGen(_ * -1 * 0.002 * picWidth + centerOffset),
        EvoGen(_ * -1 * 0.002 * picWidth + centerOffset)
      ) +

      Rotation(
        EvoGen(_ * (360 / (numRects / 10)) % 360),
        EvoGen(_ * 0.002 * picWidth),
        EvoGen(_ * 0.002 * picWidth)
      )
    )
  )

}
