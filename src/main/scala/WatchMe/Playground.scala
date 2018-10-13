package WatchMe

import BildScript._
import BildScript.Fillings.SolidSurface
import BildScript.Generators.EvoGen
import BildScript.Masks.RectMask
import BildScript.Transformations.PositionTransform
import BildScript.{Bild, Color, Resolution}

import scala.language.postfixOps

object Playground extends App {

  Bild (
    SolidSurface(Color(0,1,1,1)) +

    Bild (
      PositionTransform(0, 0) +

      Bild (
        SolidSurface(Color(0,1,0,1)) +
        RectMask(0.2f, 0.2f) +
        PositionTransform(EvoGen(_%5*0.4f), EvoGen(x=>Math.floor(x/5).toFloat*0.4f))
        //  PositionTransform(EvoGen(_%7*0.4), EvoGen(x=>(x/7).floor*0.4)) +
      ) * 100

    )

  )
    .raster(Resolution(1000, 1000), 2)
    .output("image.png")

}
