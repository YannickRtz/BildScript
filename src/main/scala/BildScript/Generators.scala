package BildScript

import scala.language.implicitConversions

object Generators {

  case class CountGen(currentNumber: Double, stepFunc: Double => Double) extends Gen[Double] {
    override def nextGen: Gen[Double] = CountGen(stepFunc(currentNumber), stepFunc)
    override def get: Double = currentNumber
  }

  case class EvoGen(evoFunc: Int => Double, evoCount: Int = 0) extends Gen[Double] {
    override def nextGen: Gen[Double] = EvoGen(evoFunc, evoCount + 1)
    override def get: Double = evoFunc(evoCount)
  }

}
