package WatchMe

import BildScript.Fillings.SolidSurface
import BildScript.Generators.EvoGen
import BildScript.Masks.RectMask
import BildScript.Transformations.PositionTransform
import BildScript.{Bild, Resolution, _}

import scala.language.postfixOps

object Playground extends App {

  println("Initialization...")


  Bild {
    SolidSurface(GColor("ffffff")) +
    Bild {
      SolidSurface(GColor(
        EvoGen(_ * 30 % 255, 0),
        EvoGen(_ * 33 % 255, 4),
        EvoGen(_ * 32 % 255, 6))
      ) +
      RectMask(0.041, 0.041) +
      PositionTransform(
        EvoGen(_ % 50 * 0.04),
        EvoGen(x => Math.floor(x / 50) * 0.04)
      )
    } * 2500
  }


    .raster(Resolution(1000, 1000), 2)
    .output("image.png")

}
