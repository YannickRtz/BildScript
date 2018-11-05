package BildScript

import scala.language.implicitConversions

object Generators {

  // TODO: Add support for generators on different levels
  // TODO: Add support for random numbers

  case class StepGen(currentNumber: Double, stepFunc: Double => Double) extends Gen[Double] {
    override def next: Gen[Double] = StepGen(stepFunc(currentNumber), stepFunc)
    override def get: Double = currentNumber
  }

  case class EvoGen(evoFunc: Int => Double, evoCount: Int = 0) extends Gen[Double] {
    override def next: Gen[Double] = EvoGen(evoFunc, evoCount + 1)
    override def get: Double = evoFunc(evoCount)
  }

}
