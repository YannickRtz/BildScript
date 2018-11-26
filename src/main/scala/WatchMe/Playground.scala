package WatchMe
import BildPackage.Fillings.SolidSurface
import BildPackage.Generators.EvoGen
import BildPackage.Masks.RectMask
import BildPackage.Transformations.{Translation, Rotation}
import BildPackage._

import scala.language.postfixOps

object Playground extends App {

  BildScript(
    resolutionX = 1000,
    resolutionY = 1000,
    width = 3,
    fileName = "image.png"
  )(

    SolidSurface(HEX("222222")) +

    Bild (
      Translation(0.5, 0.3) +

      2 * Bild (
        15 * Bild (
          SolidSurface(RGBA256(
            EvoGen(255 / 15 * _),
            255,
            EvoGen(255 - 255 / 15 * _),
            35
          )) +
          RectMask(1, 1) +
          Translation(EvoGen(_ * 0.075), EvoGen(_ * 0.1)) +
          Translation(0, EvoGen(_ * 1, Levels(1))) +
          Rotation(EvoGen(_ * 5 - 25), 0.5, 0.5)
        )
      )

      /*Bild (
        15 * Bild (
          SolidSurface(RGBA256(
            EvoGen(255 / 15 * _),
            EvoGen(255 - 255 / 15 * _),
            255, 35
          )) +
          RectMask(1, 1) +
          Translation(EvoGen(_ * 0.075), EvoGen(1.4 - _ * 0.1)) +
          Rotation(EvoGen(_ * 5 - 25), 0.5, 0.5)
        )
      )*/
    )
  )

}