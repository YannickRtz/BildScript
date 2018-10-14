
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
    def sample(p: Point): Color
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

  case class Point(x: Double, y: Double) {
    def + (p2: Point) = Point(x + p2.x, y + p2.y)
    def - (p2: Point) = Point(x - p2.x, y - p2.y)
  }

  case class Resolution(x: Int, y: Int)

  class FixedDoubleGen(number: Double) extends Gen[Double] {
    override def nextGen: Gen[Double] = new FixedDoubleGen(number)
    override def get: Double = number
  }

  implicit def genGet[A](r: Gen[A]): A = r.get
  implicit def number2FixedIntGen(n: Int): Gen[Double] = new FixedDoubleGen(n)
  implicit def number2FixedDoubleGen(n: Double): Gen[Double] = new FixedDoubleGen(n)

}
