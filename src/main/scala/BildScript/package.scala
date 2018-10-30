
import java.awt.image.BufferedImage

import scala.language.implicitConversions

package object BildScript {

  type ARGB = Int

  trait Addable {
    def next: Addable
    def + (a: Addable): Seq[Addable] = Seq(this, a)
    def * (n: Int): Seq[Addable] = Seq.range(1, n).scanLeft(this)((a, _) => a.next)
    def + : Addable = this
  }

  class RichAddableList(l: Seq[Addable]) {
    def + (a: Addable): Seq[Addable] = l :+ a
    def + (x: Seq[Addable]): Seq[Addable] = l ++ x
    def + : Seq[Addable] = l
  }

  implicit def list2RichAddableList(l: Seq[Addable]): RichAddableList = new RichAddableList(l)
  implicit def addable2RichAddableList(a: Addable): RichAddableList = new RichAddableList(Seq(a))

  trait Mask extends Addable {
    val boundingBoxDimensions: Point
    def next: Mask
    def test(p: Point): Boolean
  }

  trait Transformation extends Addable {
    def next: Transformation
    def exec(p: Point): Point
    def execReverse(p: Point): Point
  }

  trait LocalTransform extends Transformation {
    val pivotPoint: Point
  }

  trait Filling extends Addable {
    def next: Filling
    def trace(p: Point): Color
  }

  trait Gen[A] {
    def nextGen: Gen[A]
    def get: A
  }

  case class Point(x: Double, y: Double) {
    // println("new point")
    def + (p2: Point) = Point(x + p2.x, y + p2.y)
    def - (p2: Point) = Point(x - p2.x, y - p2.y)
  }

  case class Resolution(x: Int, y: Int) {
    // println("new resolution")
  }

  class FixedDoubleGen(number: Double) extends Gen[Double] {
    // println("new fixed double gen")
    override def nextGen: Gen[Double] = this
    override def get: Double = number
  }

  implicit def genGet[A](r: Gen[A]): A = r.get
  implicit def number2FixedIntGen(n: Int): Gen[Double] = new FixedDoubleGen(n)
  implicit def number2FixedDoubleGen(n: Double): Gen[Double] = new FixedDoubleGen(n)

  case class GColor(red: Gen[Double], green: Gen[Double], blue: Gen[Double], alpha: Gen[Double] = 1) {
    def get: Color = Color(red, green, blue, alpha)
    def next: GColor = GColor(red.nextGen, green.nextGen, blue.nextGen, alpha.nextGen)
  }

  // We don't implicitly convert GColors to prevent confusion in DSL Layer
  // implicit def colorToGColor(c: Color): GColor = GColor(c.red, c.green, c.blue, c.alpha)

}
