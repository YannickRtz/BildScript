package WatchMe
import BildPackage.Fillings._
import BildPackage.Generators._
import BildPackage.Masks._
import BildPackage.Transformations._
import BildPackage._

import scala.language.postfixOps

object Playground extends App {

  BildScript(
    resolutionX = 1000,
    resolutionY = 1000,
    width = 100,
    fileName = "image.png",
    doAntiAliasing = true
  )(
    SolidSurface(HEX("222222")) +
    32 * Bild (
      Translation(30, 35) +
      Scale(FromIndex(x=> 0.4 * (5 - x * 0.13)),
        FromIndex(x=> 0.4 * (5 - x * 0.13)), 20, 25) +
      2 * Bild (
        SolidSurface(
          HSV(FromIndex(_ * 43 % 111, Levels(1)), 0.8, 1)
        ) +
        CircMask(8) +
        Translation(FromIndex(_ * 25), 0)
      ) +
      Bild (
        SolidSurface(
          HSV(FromIndex(_ * 43 % 111, Levels(1)), 0.8, 1)
        ) +
        CircMask(14) +
        Translation(6, 10)
      )
    )
  )

}