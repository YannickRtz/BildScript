package BildScript

import scala.language.implicitConversions

object Generators {

  case class CountGen(currentNumber: Double, stepFunc: Double => Double) extends Gen[Double] {
    // println("new count gen")
    override def nextGen: Gen[Double] = CountGen(stepFunc(currentNumber), stepFunc)
    override def get: Double = currentNumber
  }

  // TODO: It's kind of hard to remember what these do, is there a smarter naming scheme?

  case class EvoGen(evoFunc: Int => Double, evoCount: Int = 0) extends Gen[Double] {
    // println("new evo gen")
    override def nextGen: Gen[Double] = EvoGen(evoFunc, evoCount + 1)
    override def get: Double = evoFunc(evoCount)
  }

}
