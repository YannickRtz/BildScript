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
        SolidSurface(Color(0,1,0,0.5f)) +
        RectMask(0.02, 0.02) +
        PositionTransform(EvoGen(_%30*0.04), EvoGen(x=>Math.floor(x/30)*0.04))
        //PositionTransform(EvoGen(_%7*0.4), EvoGen(x=>(x/7).floor*0.4)) +
      ) * 100

    )

  )
    .raster(Resolution(1900, 2500), 2)
    .output("image.png")

}
