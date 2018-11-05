package BildScript

object Transformations {

  case class PositionTransform(x: Gen[Double], y: Gen[Double]) extends Transformation {
    val vector = Point(x, y)
    override def next: Transformation = PositionTransform(x.next, y.next)
    override def exec(p: Point): Point = p + vector
    override def execReverse(p: Point): Point = p - vector
  }

  case class RotationTransform(degree: Gen[Double], x: Gen[Double] = 0, y: Gen[Double] = 0) extends LocalTransform {
    val radian: Double = degree * (Math.PI / 180)
    override val pivotPoint: Point = Point(x, y)
    override def next: Transformation = RotationTransform(degree.next, x.next, y.next)
    override def exec(p: Point): Point = rotate(p, -1 * radian)
    override def execReverse(p: Point): Point = rotate(p, radian)

    def rotate(p: Point, r: Double): Point =
      Point(
        p.x * Math.cos(r) - p.y * Math.sin(r),
        p.x * Math.sin(r) + p.y * Math.cos(r)
      )
  }

}
