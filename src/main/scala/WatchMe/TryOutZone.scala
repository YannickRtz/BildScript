package WatchMe
import BildPackage.Fillings.SolidSurface
import BildPackage.Generators.FromIndex
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

  BildScript(
    resolutionX = picWidth * 2,
    resolutionY = picWidth * 2,
    width = picWidth + 100,
    fileName = "image.png"
  )(

    SolidSurface(HEX("111111")) +

    numRects * Bild (

      SolidSurface(
        HSV(FromIndex(_ * (360 / (numRects / 10)) % 360), 1, 1)
      ) +

      RectMask(rectWidth, rectWidth) +

      Translation(
        FromIndex(_ * -1 * 0.002 * picWidth + centerOffset),
        FromIndex(_ * -1 * 0.002 * picWidth + centerOffset)
      ) +

      Rotation(
        FromIndex(_ * (360 / (numRects / 10)) % 360),
        FromIndex(_ * 0.002 * picWidth),
        FromIndex(_ * 0.002 * picWidth)
      )
    )
  )

}
