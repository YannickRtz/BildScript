package WatchMe
import BildPackage.Fillings._
import BildPackage.Generators._
import BildPackage.Masks._
import BildPackage.Transformations._
import BildPackage._

import scala.language.postfixOps

object Playground extends App {

  val numCircles = 27

  BildScript(
    resolutionX = 1000,
    resolutionY = 1000,
    width = 100,
    fileName = "image.png"
  )(
    SolidSurface(HEX("333333")) +
    Bild (
      Translation(10, 10) +
      numCircles * Bild (
        CircMask(EvoGen(40 - 1.5 * _)) +
        SolidSurface(HSV(
          EvoGen(x=> (200 + x * 9) % 360),
          0.7,
          EvoGen(_ * (1f / 20))
        )) +
        Translation(
          EvoGen(_ * 1),
          EvoGen(_ * 0.7)
        )
      )
    )
  )

}