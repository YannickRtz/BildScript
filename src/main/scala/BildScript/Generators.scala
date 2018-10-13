package BildScript

import scala.language.implicitConversions

object Generators {

  case class CountGen(currentNumber: Float, stepFunc: Float => Float) extends Gen[Float] {
    println("new count gen")
    override def nextGen: Gen[Float] = CountGen(stepFunc(currentNumber), stepFunc)
    override def get: Float = currentNumber
  }

  case class EvoGen(evoFunc: Int => Float, evoCount: Int = 0) extends Gen[Float] {
    println("new evo gen")
    override def nextGen: Gen[Float] = EvoGen(evoFunc, evoCount + 1)
    override def get: Float = evoFunc(evoCount)
  }

}
