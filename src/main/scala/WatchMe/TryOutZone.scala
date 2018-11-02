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
      PositionTransform(0, 150) +
      RotationTransform(45, 0, 0) +
      SolidSurface(GColor("abcdef")) +
      RectMask(40, 40) +
      PositionTransform(
        EvoGen(_ % 4 * 100),
        EvoGen(x => Math.floor(x / 4) * 100)
      ) +
      RotationTransform(EvoGen(_ * (360 / 15)), 20, 20) +
    } * (4 * 4)
  }

    .raster(Resolution(500, 500), 500)
    .output("image.png")
}
