package BildPackage

import scala.language.implicitConversions

object Generators {

/*  case class StepGen(currentNumber: Double, stepFunc: Double => Double, level: Int = 0) extends Gen[Double] {
    override def next: Gen[Double] = StepGen(stepFunc(currentNumber), stepFunc)
    override def get: Double = currentNumber
  }*/

  case class EvoGen(evoFunc: Int => Double, level: Int = 0) extends Gen[Double] {
    var calculated = false
    var result: Double = 0
    override def next: Gen[Double] = EvoGen(evoFunc, level)
    override def get(tc: Seq[Int]): Double = {
      if (calculated) result
      else {
        println(tc + " LEVEL " + level)
        result = evoFunc(tc.reverse(level))
        calculated = true
        result
      }
    }
  }

}
