package BildScript

object Masks {

  case class RectMask(widthGen: Gen[Double], heightGen: Gen[Double]) extends Mask {

    val width: Double = widthGen.get
    val height: Double = heightGen.get

    override val boundingBoxDimensions: Point = Point(width, height)

    override def next: RectMask = RectMask(widthGen.next, heightGen.next)
    override def test(p: Point): Boolean = {
      p.x < width && p.x > 0 && p.y < height && p.y > 0
    }
  }

}
