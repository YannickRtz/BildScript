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
    SolidSurface(HEX("111111")) +
    1000 * Bild (
      Translation(SimpleRnd(_ * 100 - 5), SimpleRnd(_ * 100)) +
      4 * Bild (
        Scale(SimpleRnd(
          x=> Math.abs(x * 0.17),
          gaussian = true, Levels(1),
          parentSeed = 123
        )) +
        Scale(
          FromIndex(x=> (1+x)%2*0.14+0.5),
          FromIndex(x=> (1+x)%2*0.14+0.5),
          11, 0) +
        Rotation(FromIndex(_ * 45), 10.65, 0) +
        Scale(0.15, 1, 10, 10) +
        SolidSurface(HSVA(
          SimpleRnd(_ * 50 + 10, levels = Levels(1)),
          0.5, 1,
          SimpleRnd(x=> Math.abs(x * 0.13),
            gaussian = true, Levels(1),
            parentSeed = 123))) +
        RectMask(20) +
        Rotation(45)
      )
    )
  )

}