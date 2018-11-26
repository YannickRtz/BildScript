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

}
