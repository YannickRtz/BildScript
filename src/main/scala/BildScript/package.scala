
import scala.language.implicitConversions

package object BildScript {

  type ARGB = Int

  sealed trait Addable {
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

  trait Drawable extends Addable {
    def next: Drawable
    def sample(p: Point): ARGB
  }

  trait Mask extends Addable {
    def next: Mask
    def test(p: Point): Boolean
  }

  trait Transformation extends Addable {
    def next: Transformation
    def exec(p: Point): Point
  }

  trait Filling extends Drawable {
    def next: Filling
  }

  trait Gen[A] {
    def nextGen: Gen[A]
    def get: A
  }

  case class Point(var x: Float, var y: Float) {
    println("new point")
    def + (p2: Point): Point = {
      x = x + p2.x
      y = y + p2.y
      this
    }
    def - (p2: Point): Point = {
      x = x - p2.x
      y = y - p2.y
      this
    }
  }

  case class Resolution(x: Int, y: Int)

  class FixedFloatGen(number: Float) extends Gen[Float] {
    println("new float gen")
    override def nextGen: Gen[Float] = new FixedFloatGen(number)
    override def get: Float = number
  }

  implicit def genGet[A](r: Gen[A]): A = r.get
  implicit def number2FixedIntGen(n: Int): Gen[Float] = new FixedFloatGen(n)
  implicit def number2FixedFloatGen(n: Float): Gen[Float] = new FixedFloatGen(n)

  def ARGBalpha(c: ARGB): ARGB = {
    (c>>24)&255
  }

  def ARGBoverlay(c1: ARGB, c2: ARGB): ARGB = {
    c2
  }

}
