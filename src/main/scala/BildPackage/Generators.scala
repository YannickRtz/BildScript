package BildPackage

import scala.language.implicitConversions
import scala.util.Random

object Generators {

  case class FromIndex(evoFunc: (Double, Double, Double, Double) => Double, levels: Levels) extends Gen[Double] {
    val filledLvl: Seq[Int] = levels.list ++ Seq(0, 0, 0, 0).slice(0, 4 - levels.list.length)
    override def next: Gen[Double] = FromIndex(evoFunc, levels)
    override def generate(tc: Seq[Int]): Double = {
      val ct = tc.reverse
      evoFunc(ct(filledLvl.head), ct(filledLvl(1)), ct(filledLvl(2)), ct(filledLvl(3)))
    }
  }

  case class SimpleRnd(evoFunc: (Double, Double, Double, Double) => Double, gaussian: Boolean,
                       levels: Levels, parentSeed: Int) extends Gen[Double] {
    val filledLvl: Seq[Int] = levels.list ++ Seq(0, 0, 0, 0).slice(0, 4 - levels.list.length)
    override def next: Gen[Double] = SimpleRnd(evoFunc, gaussian, levels, parentSeed)
    override def generate(tc: Seq[Int]): Double = {
      val ct = tc.reverse
      evoFunc(
        makeRandomNumber(2027, ct, filledLvl.head),
        makeRandomNumber(1231, ct, filledLvl(1)),
        makeRandomNumber(1543, ct, filledLvl(2)),
        makeRandomNumber(1039, ct, filledLvl(3))
      )
    }
    private def makeRandomNumber(rootSeed: Int, ct: Seq[Int], level: Int): Double = {
      val tcSeed = ct.drop(level).foldLeft("")((prev, next) => {prev + next}).take(18).toLong
      val rnd = new Random(parentSeed + rootSeed - tcSeed)
      if (gaussian) rnd.nextGaussian()
      else rnd.nextDouble()
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

  object SimpleRnd {
    def apply(evoFunc: (Double, Double, Double) => Double, gaussian: Boolean, levels: Levels): SimpleRnd = {
      require(levels.list.length == 3)
      val filledEvoFunc = (x1: Double, x2: Double, x3: Double, x4: Double) => evoFunc(x1, x2, x3)
      val parentSeed = new Random().nextInt()
      SimpleRnd(filledEvoFunc, gaussian, levels, parentSeed)
    }

    def apply(evoFunc: (Double, Double) => Double, gaussian: Boolean, levels: Levels): SimpleRnd = {
      require(levels.list.length == 2)
      val filledEvoFunc = (x1: Double, x2: Double, _: Double, _: Double) => evoFunc(x1, x2)
      val parentSeed = new Random().nextInt()
      SimpleRnd(filledEvoFunc, gaussian, levels, parentSeed)
    }

    def apply(evoFunc: Double => Double, gaussian: Boolean = false, levels: Levels = Levels(0),
              parentSeed: Int = new Random().nextInt()): SimpleRnd = {
      require(levels.list.length == 1)
      val filledEvoFunc = (x1: Double, _: Double, _: Double, _: Double) => evoFunc(x1)
      SimpleRnd(filledEvoFunc, gaussian, levels, parentSeed)
    }
  }

}
