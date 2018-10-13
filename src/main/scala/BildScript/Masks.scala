package BildScript

object Masks {

  case class RectMask(width: Gen[Double], height: Gen[Double]) extends Mask {
    override def next: RectMask = RectMask(width.nextGen, height.nextGen)
    override def test(p: Point): Boolean = {
      p.x < width && p.x > 0 && p.y < height && p.y > 0
    }
  }

}
