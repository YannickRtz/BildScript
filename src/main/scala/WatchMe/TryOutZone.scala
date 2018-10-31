package WatchMe
import BildScript.Fillings.SolidSurface
import BildScript.Generators.EvoGen
import BildScript.Masks.RectMask
import BildScript.Transformations.{PositionTransform, RotationTransform}
import BildScript.{Bild, Resolution, _}

import scala.language.postfixOps
object TryOutZone extends App {

  // Rotation demonstration:


  Bild {
    SolidSurface(GColor("111111")) +

    Bild {
      RotationTransform(2, 0, 0) +
      SolidSurface(GColor("abcdef")) +
      RectMask(40, 40) +
      PositionTransform(30, 60) +
      PositionTransform(
        EvoGen(_ % 4 * 100),
        EvoGen(x => Math.floor(x / 4) * 100)
      ) +
      RotationTransform(EvoGen(_ * 6), 20, 20) +
    } * (4 * 4)

  }

    .raster(Resolution(300, 500), 400)
    .output("image.png")
}
