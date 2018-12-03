package WatchMe
import BildPackage.Fillings._
import BildPackage.Generators._
import BildPackage.Masks._
import BildPackage.Transformations._
import BildPackage._

import scala.language.postfixOps

object Playground extends App {

  BildScript(
    resolutionX = 500,
    resolutionY = 500,
    width = 130,
    fileName = "image.png"
  )(
    SolidSurface(HEX("222222")) +
    8 * Bild (
      Translation(25, 25) +
      Rotation(EvoGen(_ * -45), 40, 40) +
      Translation(10, 15) +
      27 * Bild (
        CircMask(EvoGen(1 * _)) +
        SolidSurface(HSVA(300, 0.4, 1, 0.1)) +
        Translation(
          EvoGen(_ * 1),
          EvoGen(_ * 0.7)
        )
      )
    )
  )

}