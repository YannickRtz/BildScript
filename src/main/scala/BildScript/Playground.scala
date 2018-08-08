package BildScript

import BildScript.Fillings.SolidSurface
import BildScript.Generators.CountGen
import BildScript.Masks.RectMask
import BildScript.Transformations.PositionTransform

import scala.language.{implicitConversions, postfixOps}

object Playground extends App {

  Bild (
    SolidSurface(Color(0,1,1,1)) +

    Bild (
      PositionTransform(0, CountGen(0, _+0.4)) +

      Bild (
        SolidSurface(Color(0,1,0,0.5f)) +
        RectMask(0.2, 0.2) +
        PositionTransform(CountGen(0.1, _+0.3), 0)
      )

    )

  )
    .raster(Resolution(256, 512), 2)
    .output("image.png")

}

/*

* ##### ADDABLES:
* Bild
* Filling (Solid, Gradient, Picture...)
* Mask (Square, Star, Triangle...)
* Transformation (Scale, Position, Rotation, Anchor Point)
* Effekt (Opacity, Grain...)
*
* Generators
*
* */