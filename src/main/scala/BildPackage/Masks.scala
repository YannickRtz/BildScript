package BildPackage

object Masks {

  case class RectMask(widthGen: Gen[Double], heightGen: Gen[Double]) extends Mask {

    // val width: Double = widthGen.get
    // val height: Double = heightGen.get

    override def boundingBoxDimensions(tc: Seq[Int]): Point = Point(widthGen.get(tc), heightGen.get(tc))

    override def next: RectMask = RectMask(widthGen.next, heightGen.next)
    override def test(p: Point, tc: Seq[Int]): Boolean = {
      p.x < widthGen.get(tc) && p.x > 0 && p.y < heightGen.get(tc) && p.y > 0
    }
  }

}
