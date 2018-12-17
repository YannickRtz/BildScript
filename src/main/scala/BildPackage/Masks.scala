package BildPackage

object Masks {

  case class RectMask(widthGen: Gen[Double], heightGen: Gen[Double]) extends Mask {
    override lazy val boundingBoxDimensions: Point = Point(widthGen, heightGen)
    override def walk(tc: Seq[ARGB]): Unit = {
      widthGen.walk(tc)
      heightGen.walk(tc)
    }
    override def next: RectMask = RectMask(widthGen.next, heightGen.next)
    override def test(p: Point): Boolean = {
      p.x < widthGen && p.x > 0 && p.y < heightGen && p.y > 0
    }
  }

  object RectMask {
    def apply(widthGen: Gen[Double]): RectMask = RectMask(widthGen, widthGen.next)
  }

  case class CircMask(radius: Gen[Double]) extends Mask {
    // TODO: Input should probably be diameter
    override lazy val boundingBoxDimensions: Point = Point(radius * 2, radius * 2)
    override def walk(tc: Seq[ARGB]): Unit = radius.walk(tc)
    override def next: CircMask = CircMask(radius.next)
    override def test(p: Point): Boolean = {
      val distanceVector = Point(p.x - radius, p.y - radius)
      distanceVector.length < radius
    }
  }

}
