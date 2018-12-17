package BildPackage

object Transformations {

  case class Translation(x: Gen[Double], y: Gen[Double]) extends Transformation {
    override def next: Transformation = Translation(x.next, y.next)
    override def walk(tc: Seq[Int]): Unit = {
      x.walk(tc)
      y.walk(tc)
    }
    override def exec(p: Point): Point = Point(p.x + x, p.y + y)
    override def execReverse(p: Point): Point = Point(p.x - x, p.y - y)
  }

  case class Rotation(degree: Gen[Double], x: Gen[Double] = 0, y: Gen[Double] = 0) extends LocalTransform {
    lazy val radian: Double = degree * (Math.PI / 180)
    override lazy val pivotPoint: Point = Point(x, y)
    override def next: Transformation = Rotation(degree.next, x.next, y.next)
    override def walk(tc: Seq[Int]): Unit = {
      degree.walk(tc)
      x.walk(tc)
      y.walk(tc)
    }
    override def exec(p: Point): Point = rotate(p, -1 * radian)
    override def execReverse(p: Point): Point = rotate(p, radian)

    def rotate(p: Point, r: Double): Point =
      Point(
        p.x * Math.cos(r) - p.y * Math.sin(r),
        p.x * Math.sin(r) + p.y * Math.cos(r)
      )
  }

  case class Scale(xScale: Gen[Double], yScale: Gen[Double], xPivot: Gen[Double] = 0,
                   yPivot: Gen[Double] = 0) extends LocalTransform {
    override val pivotPoint: Point = Point(xPivot, yPivot)
    override def next: Transformation = Scale(xScale.next, yScale.next, xPivot.next, yPivot.next)
    override def walk(tc: Seq[ARGB]): Unit = {
      xScale.walk(tc)
      yScale.walk(tc)
      xPivot.walk(tc)
      yPivot.walk(tc)
    }
    override def exec(p: Point): Point = Point(p.x * xScale, p.y * yScale)
    override def execReverse(p: Point): Point = Point(p.x / xScale, p.y / yScale)
  }

  object Scale {
    def apply(scaleFactor: Gen[Double]): Scale = Scale(scaleFactor, scaleFactor.next)
  }

}
