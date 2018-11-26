package BildPackage

import scala.language.implicitConversions

object Generators {

/*  case class StepGen(currentNumber: Double, stepFunc: Double => Double, level: Int = 0) extends Gen[Double] {
    override def next: Gen[Double] = StepGen(stepFunc(currentNumber), stepFunc)
    override def get: Double = currentNumber
  }*/

  case class EvoGen(evoFunc: Int => Double, levels: Levels = Levels(0)) extends Gen[Double] {
    override def next: Gen[Double] = EvoGen(evoFunc, levels)
    override def generate(tc: Seq[Int]): Double = evoFunc(tc.reverse(levels(0)))
    // TODO: Support multiple levels
  }

}
