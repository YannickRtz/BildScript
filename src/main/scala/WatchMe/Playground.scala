package WatchMe
import BildPackage.Fillings.SolidSurface
import BildPackage.Generators.EvoGen
import BildPackage.Masks.RectMask
import BildPackage.Transformations.{Translation, Rotation}
import BildPackage._

import scala.language.postfixOps

object Playground extends App {

  BildScript(
    resolutionX = 500,
    resolutionY = 500,
    width = 100,
    fileName = "image.png"
  )(

    SolidSurface(HEX("222222")) +

    Bild (
      Translation(6, 6) +

      9 * Bild (
        9 * Bild (
          SolidSurface(RGB256(
            EvoGen(28 * _, Levels(1)),
            100,
            EvoGen(28 * _, Levels(0))
          )) +
          RectMask(5, 5) +
          Translation(EvoGen(_ * 10), EvoGen(_ * 10, Levels(1))) +
          Rotation(EvoGen((x,y) => x + y * 5, Levels(0, 1)))
        )
      )
    )

  )

}