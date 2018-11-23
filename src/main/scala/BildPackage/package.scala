
import scala.language.implicitConversions

package object BildPackage {

  type ARGB = Int

  case class BildScript(resolutionX: Int, resolutionY: Int, width: Double, fileName: String) {
    def apply(l: Seq[Addable]): Unit = Bild(l).raster(resolutionX, resolutionY, width, fileName)
    def apply(a: Addable): Unit = Bild(a).raster(resolutionX, resolutionY, width, fileName)
  }

  trait Addable {
    def next: Addable
    def + (a: Addable): Seq[Addable] = Seq(this, a)
    def * (n: Int): Seq[Addable] = Seq.range(1, n).scanLeft(this)((a, _) => a.next)
    def + : Addable = this
  }

  class RichAddableInt(i: Int) {
    def * (a: Addable): Seq[Addable] = Seq.range(1, i).scanLeft(a)((b, _) => b.next)
  }

  implicit def int2RichAddableInt(i: Int): RichAddableInt = new RichAddableInt(i)

  class RichAddableList(l: Seq[Addable]) {
    def + (a: Addable): Seq[Addable] = l :+ a
    def + (x: Seq[Addable]): Seq[Addable] = l ++ x
    def + : Seq[Addable] = l
  }

  implicit def list2RichAddableList(l: Seq[Addable]): RichAddableList = new RichAddableList(l)
  implicit def addable2RichAddableList(a: Addable): RichAddableList = new RichAddableList(Seq(a))

  trait Mask extends Addable {
    def boundingBoxDimensions(tc: Seq[Int]): Point
    def next: Mask
    def test(p: Point, tc: Seq[Int]): Boolean
  }

  trait Transformation extends Addable {
    def next: Transformation
    def exec(p: Point, tc: Seq[Int]): Point
    def execReverse(p: Point, tc: Seq[Int]): Point
  }

  trait LocalTransform extends Transformation {
    def pivotPoint(tc: Seq[Int]): Point
  }

  trait Filling extends Addable {
    def next: Filling
    def trace(p: Point, tc: Seq[Int]): Color
  }

  trait Gen[A] {
    val level: Int
    def next: Gen[A]
    def get(treeCoordinates: Seq[Int]): A
  }

  case class Point(x: Double, y: Double) {
    def + (p2: Point) = Point(x + p2.x, y + p2.y)
    def - (p2: Point) = Point(x - p2.x, y - p2.y)

    def applyTransforms(transformations: Seq[Transformation], tc: Seq[Int]): Point =
      transformations.foldLeft(this)((prev, t) => t.exec(prev, tc))
    def applyTransformsReverse(transformations: Seq[Transformation], tc: Seq[Int]): Point =
      transformations.foldRight(this)((t, prev) => t.execReverse(prev, tc))
  }

  class FixedDoubleGen(number: Double) extends Gen[Double] {
    val level = 0
    override def next: Gen[Double] = this
    override def get(treeCoordinates: Seq[Int]): Double = number
  }

  // TODO: Reactivate this implicit conversion but with treecoordinates?
  // implicit def genGet[A](r: Gen[A]): A = r.get
  implicit def number2FixedIntGen(n: Int): Gen[Double] = new FixedDoubleGen(n)
  implicit def number2FixedDoubleGen(n: Double): Gen[Double] = new FixedDoubleGen(n)

  case class RGBA(red: Gen[Double], green: Gen[Double], blue: Gen[Double], alpha: Gen[Double], level: Int = 0) extends Gen[Color] {
    override def next: Gen[Color] = RGBA(red.next, green.next, blue.next, alpha.next)
    override def get(tc: Seq[Int]): Color = Color(red.get(tc), green.get(tc), blue.get(tc), alpha.get(tc))
  }

  object RGB {
    def apply(red: Gen[Double], green: Gen[Double], blue: Gen[Double]): RGBA =
      RGBA(red, green, blue, 1)
  }

  case class RGBA256(red: Gen[Double], green: Gen[Double], blue: Gen[Double], alpha: Gen[Double], level: Int = 0) extends Gen[Color] {
    def next: Gen[Color] = RGBA256(red.next, green.next, blue.next, alpha.next)
    def get(tc: Seq[Int]): Color = Color(
      red.get(tc) / 255.0,
      green.get(tc) / 255.0,
      blue.get(tc) / 255.0,
      alpha.get(tc) / 255.0
    )
  }

  object RGB256 {
    def apply(red: Gen[Double], green: Gen[Double], blue: Gen[Double]): RGBA256 =
      RGBA256(red, green, blue, 255)
  }

  case class HSVA(hg: Gen[Double], sg: Gen[Double], vg: Gen[Double], ag: Gen[Double], level: Int = 0) extends Gen[Color] {
    override def next: Gen[Color] = HSVA(hg.next, sg.next, vg.next, ag.next)
    override def get(tc: Seq[Int]): Color = {
      // Algorithm from https://de.wikipedia.org/wiki/HSV-Farbraum
      val h = hg.get(tc)
      val s = sg.get(tc)
      val v = vg.get(tc)
      val a = ag.get(tc)
      val hi = Math.floor(h / 60)
      val f = h / 60 - hi
      val p = v * (1 - s)
      val q = v * (1 - s * f)
      val t = v * (1 - s * (1 - f))
      hi match {
        case 1 => Color(q, v, p, a)
        case 2 => Color(p, v, t, a)
        case 3 => Color(p, q, v, a)
        case 4 => Color(t, p, v, a)
        case 5 => Color(v, p, q, a)
        case _ => Color(v, t, p, a)
      }
    }
  }

  object HSV {
    def apply(h: Gen[Double], s: Gen[Double], v: Gen[Double]): HSVA =
      HSVA(h, s, v, 1)
  }

  object HEX {
    def apply(hex: String): RGBA256 = {
      require(hex.length == 6 || hex.length == 8)
      val red = Integer.parseInt(hex.slice(0, 2), 16)
      val green = Integer.parseInt(hex.slice(2, 4), 16)
      val blue = Integer.parseInt(hex.slice(4, 6), 16)
      val alpha =
        if (hex.length >= 8) Integer.parseInt(hex.slice(6, 8), 16)
        else 255
      RGBA256(red, green, blue, alpha)
    }
  }

}
