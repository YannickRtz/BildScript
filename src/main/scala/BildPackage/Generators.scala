package BildPackage

import scala.language.implicitConversions

object Generators {

  case class FromIndex(evoFunc: (Double, Double, Double, Double) => Double, levels: Levels) extends Gen[Double] {
    val filledLvl: Seq[Int] = levels.list ++ Seq(0, 0, 0, 0).slice(0, 4 - levels.list.length)
    override def next: Gen[Double] = FromIndex(evoFunc, levels)
    override def generate(tc: Seq[Int]): Double = {
      val ct = tc.reverse
      evoFunc(ct(filledLvl.head), ct(filledLvl(1)), ct(filledLvl(2)), ct(filledLvl(3)))
    }
  }

  object FromIndex {
    // Enable up to four arguments in function. For default arguments details see:
    // https://stackoverflow.com/questions/4652095/why-does-the-scala-compiler-disallow-overloaded-methods-with-default-arguments
    def apply(evoFunc: (Double, Double, Double) => Double, levels: Levels): FromIndex = {
      require(levels.list.length == 3)
      val filledEvoFunc = (x1: Double, x2: Double, x3: Double, x4: Double) => evoFunc(x1, x2, x3)
      FromIndex(filledEvoFunc, levels)
    }

    def apply(evoFunc: (Double, Double) => Double, levels: Levels): FromIndex = {
      require(levels.list.length == 2)
      val filledEvoFunc = (x1: Double, x2: Double, _: Double, _: Double) => evoFunc(x1, x2)
      FromIndex(filledEvoFunc, levels)
    }

    def apply(evoFunc: Double => Double, levels: Levels = Levels(0)): FromIndex = {
      require(levels.list.length == 1)
      val filledEvoFunc = (x1: Double, _: Double, _: Double, _: Double) => evoFunc(x1)
      FromIndex(filledEvoFunc, levels)
    }
  }

}
