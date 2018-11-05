package WatchMe

import BildScript.Fillings.SolidSurface
import BildScript.Generators.EvoGen
import BildScript.Masks.RectMask
import BildScript.Transformations.{Translation, Rotation}
import BildScript._

import scala.language.postfixOps

object Playground extends App {

  BildScript(1000, 1000, 3, "image.png") (

    SolidSurface(HEX("222222")) +

    Bild (
      Translation(0.5, 0.3) +

      15 * Bild (
        SolidSurface(RGBA256(
          EvoGen(255 / 15 * _),
          255,
          EvoGen(255 - 255 / 15 * _),
          35
        )) +
          RectMask(1, 1) +
          Translation(EvoGen(_ * 0.075), EvoGen(_ * 0.1)) +
          Rotation(EvoGen(_ * 5 - 25), 0.5, 0.5)
        ) +

      15 * Bild (
        SolidSurface(RGBA256(
          EvoGen(255 / 15 * _),
          EvoGen(255 - 255 / 15 * _),
          255, 35
        )) +
          RectMask(1, 1) +
          Translation(EvoGen(_ * 0.075), EvoGen(1.4 - _ * 0.1)) +
          Rotation(EvoGen(_ * 5 - 25), 0.5, 0.5)
      )
    )
  )

}

// TODO: Should fillings know about the bounding box dimensions?
// TODO: Support basic transparancy
// TODO: Support anti aliasing
// TODO: Support for transparent masks
// TODO: Add some kind of color palettes
// TODO: Think about a way to pass options like useBoundingBox, useAntiAliasing etc.
// TODO: Support for multiple fillings and multiple masks
// TODO: Support for masking Bild objects (probably requires extra bufferedImage)
// TODO: Add Scale Tansformation
// TODO: Add Shear Transformation
// TODO: Add Reflection Transformation
// TODO: Add support for generators on different levels
// TODO: Add support for random numbers