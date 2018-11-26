package BildPackage

import scala.language.implicitConversions

object Generators {

/*  case class StepGen(currentNumber: Double, stepFunc: Double => Double, level: Int = 0) extends Gen[Double] {
    override def next: Gen[Double] = StepGen(stepFunc(currentNumber), stepFunc)
    override def get: Double = currentNumber
  }*/

  case class EvoGen(evoFunc: (Int, Int, Int, Int) => Double, levels: Levels) extends Gen[Double] {
    val filledLvl: Seq[Int] = levels.list ++ Seq(0, 0, 0, 0).slice(0, 4 - levels.list.length)
    override def next: Gen[Double] = EvoGen(evoFunc, levels)
    override def generate(tc: Seq[Int]): Double = {
      val ct = tc.reverse
      evoFunc(ct(filledLvl.head), ct(filledLvl(1)), ct(filledLvl(2)), ct(filledLvl(3)))
    }
  }

  object EvoGen {
    // Enable up to four arguments in function. For default arguments details see:
    // https://stackoverflow.com/questions/4652095/why-does-the-scala-compiler-disallow-overloaded-methods-with-default-arguments
    def apply(evoFunc: (Int, Int, Int) => Double, levels: Levels): EvoGen = {
      require(levels.list.length == 3)
      val filledEvoFunc = (x1: Int, x2: Int, x3: Int, x4: Int) => evoFunc(x1, x2, x3)
      EvoGen(filledEvoFunc, levels)
    }

    def apply(evoFunc: (Int, Int) => Double, levels: Levels): EvoGen = {
      require(levels.list.length == 2)
      val filledEvoFunc = (x1: Int, x2: Int, _: Int, _: Int) => evoFunc(x1, x2)
      EvoGen(filledEvoFunc, levels)
    }

    def apply(evoFunc: Int => Double, levels: Levels = Levels(0)): EvoGen = {
      require(levels.list.length == 1)
      val filledEvoFunc = (x1: Int, _: Int, _: Int, _: Int) => evoFunc(x1)
      EvoGen(filledEvoFunc, levels)
    }
  }

}
