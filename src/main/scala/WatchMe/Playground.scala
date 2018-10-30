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
    SolidSurface(GColor(0, 1, 1)) +
    Bild {
      SolidSurface(GColor(0, 1, 0, EvoGen(_ * 0.25, 1))) +
      RectMask(0.2, 0.2) +
      PositionTransform(EvoGen(_ % 30 * 0.4), 0.5)
    } * 4
  }


    .raster(Resolution(400, 400), 2)
    .output("image.png")

}
