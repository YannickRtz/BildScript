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
      Translation(25, 30) +
      3 * Bild (
        3 * Bild (
          3 * Bild (
            RectMask(2.5, 2.5) +
            SolidSurface(
              HSV(190, 0.9, EvoGen(1 - _ * (1f / 3), Levels(2)))
            ) +
            Translation(
              EvoGen(_ * 20 + _ * 4, Levels(0, 2)),
              EvoGen(_ * 20 - _ * 4, Levels(1, 2))
            )
          )
        )
      )
    )

  )

}