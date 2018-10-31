package BildScript

object Transformations {

  case class PositionTransform(x: Gen[Double], y: Gen[Double]) extends Transformation {
    val vector = Point(x, y)
    override def next: Transformation = PositionTransform(x.nextGen, y.nextGen)
    override def exec(p: Point): Point = p + vector
    override def execReverse(p: Point): Point = p - vector
  }

  case class RotationTransform(degree: Gen[Double], x: Gen[Double] = 0, y: Gen[Double] = 0) extends LocalTransform {
    // TODO: Either use x,y everywhere or Point() Format, but this is mixed...
    // TODO: Use degrees and maybe make radian an optional choice
    override val pivotPoint: Point = Point(x, y)
    override def next: Transformation = RotationTransform(degree.nextGen, x.nextGen, y.nextGen)
    override def exec(p: Point): Point = rotate(p, degree)
    override def execReverse(p: Point): Point = rotate(p, -degree)
    val radian: Double = degree * (Math.PI / 180)

    def rotate(p: Point, degree: Double): Point =
      Point(
        p.x * Math.cos(radian) - p.y * Math.sin(radian),
        p.x * Math.sin(radian) + p.y * Math.cos(radian)
      )
  }

}
