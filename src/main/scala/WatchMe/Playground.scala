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
    width = 130,


    fileName = "image.png"
  )(
    SolidSurface(HEX("222222")) +
    7 * Bild (
      Translation(FromIndex(-30 + Math.sin(_) * 10), FromIndex(20 * _)) +
      27 * Bild (
        Rotation(45) +
        CircMask(FromIndex(x=> Math.sin(x * 3 % 10 * 0.1) * 20)) +
        SolidSurface(HSVA(FromIndex(_ * 290 % 360), 0.5, 1, 0.7)) +
        Translation(FromIndex(_ * 4), FromIndex(_ * 4))
      )
    )
  )

}