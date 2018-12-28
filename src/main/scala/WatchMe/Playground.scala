package WatchMe
import BildPackage.Fillings._
import BildPackage.Generators._
import BildPackage.Masks._
import BildPackage.Transformations._
import BildPackage._
import Meta.Measurement

import scala.language.postfixOps

object Playground extends App {

  BildScript(
    resolutionX = 1000,
    resolutionY = 1000,
    width = 100,
    fileName = "image.png",
    doAntiAliasing = false
  )(
    SolidSurface(HEX("111111")) +
    1000 * Bild (
      Translation(SimpleRnd(_ * 100 - 5), SimpleRnd(_ * 100)) +
      4 * Bild (
        RectMask(20) +
        SolidSurface(HSVA(
          SimpleRnd(x=> Math.abs(x * 110),
            gaussian = true, Levels(1),
            parentSeed = 123),
          0.5, 1,
          SimpleRnd(x=> Math.abs(x * 0.25),
            gaussian = true, Levels(1),
            parentSeed = 123)
        )) +
        Rotation(45) +
        Scale(0.15, 1, 10, 10) +
        Rotation(FromIndex(_ * 45 + 45), 10.65, 0) +
        Scale(
          FromIndex(_ % 2 * 0.14 + 0.5),
          FromIndex(_ % 2 * 0.14 + 0.5),
          10.65, 0
        ) +
        Scale(
          SimpleRnd(x=> Math.abs(x * 0.17),
          gaussian = true, Levels(1),
          parentSeed = 123)
        )
      )
    )
  )

}