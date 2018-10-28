package BildScript

object Transformations {

  case class PositionTransform(x: Gen[Double], y: Gen[Double]) extends Transformation {
    val vector = Point(x, y)
    override def next: Transformation = PositionTransform(x.nextGen, y.nextGen)
    override def exec(p: Point): Point = p + vector
    override def execReverse(p: Point): Point = p - vector
  }

  case class RotationTransform(x: Gen[Double], y: Gen[Double], degree: Gen[Double]) extends LocalTransform {
    override val pivotPoint: Point = Point(x, y)
    override def next: Transformation = RotationTransform(x.nextGen, y.nextGen, degree.nextGen)
    override def exec(p: Point): Point = rotate(p, degree)
    override def execReverse(p: Point): Point = rotate(p, -degree)
    def rotate(p: Point, degree: Double): Point =
      Point(
        p.x * Math.cos(degree) - p.y * Math.sin(degree),
        p.x * Math.sin(degree) + p.y * Math.cos(degree)
      )
  }

}
