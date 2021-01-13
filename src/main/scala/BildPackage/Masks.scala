package BildPackage

object Masks {

  case class RectMask(widthGen: Gen[Double], heightGen: Gen[Double]) extends Mask {
    override lazy val boundingBoxDimensions: Point = Point(widthGen, heightGen)
    override def walk(tc: Seq[Int]): Unit = {
      widthGen.walk(tc)
      heightGen.walk(tc)
    }
    override def next: RectMask = RectMask(widthGen.next, heightGen.next)
    override def test(p: Point): Double = {
      if (p.x < widthGen && p.x > 0 && p.y < heightGen && p.y > 0) 1
      else 0
    }
  }

  object RectMask {
    def apply(widthGen: Gen[Double]): RectMask = RectMask(widthGen, widthGen.next)
  }

  case class CircMask(radius: Gen[Double], gradientFunc: Double => Double = _=> 1,
                      gradientGamma: Gen[Double] = 1) extends Mask {
    // TODO: Input should probably be diameter
    override lazy val boundingBoxDimensions: Point = Point(radius * 2, radius * 2)
    override def walk(tc: Seq[Int]): Unit = {
      radius.walk(tc)
      gradientGamma.walk(tc)
    }
    override def next: CircMask = CircMask(radius.next, gradientFunc, gradientGamma.next)
    override def test(p: Point): Double = {
      val distance = Point(p.x - radius, p.y - radius).length
      if (distance > radius || radius.get == 0) 0
      else Math.pow(gradientFunc(distance / radius), gradientGamma)
    }
  }

}
