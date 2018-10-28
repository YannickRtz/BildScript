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

  Bild (
    SolidSurface(Color(1,0,0,1)) +

      Bild (
        PositionTransform(0.75, 0.4) +
        RotationTransform(0, 0, 0.3) +

          Bild (
            SolidSurface(Color(0,1,1,1)) +
              RectMask(0.1, 0.1) +
              PositionTransform(EvoGen(_%5*0.2), EvoGen(x=>Math.floor(x/5)*0.2)) +
              RotationTransform(0, 0, 0.75)
          ) * 25

      )

  )
    .raster(Resolution(1000, 1000), 2)
    .output("image.png")

}
