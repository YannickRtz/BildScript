package WatchMe

import BildScript.Fillings.SolidSurface
import BildScript.Generators.EvoGen
import BildScript.Masks.RectMask
import BildScript.Transformations.{PositionTransform, RotationTransform}
import BildScript._

import scala.language.postfixOps

object Playground extends App {

  Canvas(1000, 1000, 3, "image.png") (

    SolidSurface(HEX("222222")) +

    Bild (
      PositionTransform(0.5, 0.3) +

      15 * Bild (
        SolidSurface(RGBA256(
          EvoGen(255 / 15 * _),
          255,
          EvoGen(255 - 255 / 15 * _),
          35
        )) +
          RectMask(1, 1) +
          PositionTransform(EvoGen(_ * 0.075), EvoGen(_ * 0.1)) +
          RotationTransform(EvoGen(_ * 5 - 25), 0.5, 0.5)
        ) +

      15 * Bild (
        SolidSurface(RGBA256(
          EvoGen(255 / 15 * _),
          EvoGen(255 - 255 / 15 * _),
          255, 35
        )) +
          RectMask(1, 1) +
          PositionTransform(EvoGen(_ * 0.075), EvoGen(1.4 - _ * 0.1)) +
          RotationTransform(EvoGen(_ * 5 - 25), 0.5, 0.5)
      )
    )
  )

}
