package WatchMe

import BildScript._
import BildScript.Fillings.SolidSurface
import BildScript.Generators.EvoGen
import BildScript.Masks.RectMask
import BildScript.Transformations.{PositionTransform, RotationTransform}
import BildScript.{Bild, Color, Resolution}

import scala.language.postfixOps

object Playground extends App {

  println("Initialization...")


  Bild {
    SolidSurface(GColor(0, 1, 1)) +
    Bild {
      SolidSurface(GColor(0, 1, 0, EvoGen(_ * 0.25, 1))) +
      RectMask(0.2, 0.2) +
      PositionTransform(EvoGen(_ % 30 * 0.4), 0.5)
    } * 4
  }

    .raster(Resolution(500, 500), 2)
    .output("image.png")

}
