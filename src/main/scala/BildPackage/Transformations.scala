package BildPackage

object Transformations {

  case class Translation(x: Gen[Double], y: Gen[Double]) extends Transformation {
    // val vector = Point(x, y)
    override def next: Transformation = Translation(x.next, y.next)
    override def exec(p: Point, tc: Seq[Int]): Point = Point(p.x + x.get(tc), p.y + y.get(tc))
    override def execReverse(p: Point, tc: Seq[Int]): Point = Point(p.x - x.get(tc), p.y - y.get(tc))
  }

  case class Rotation(degree: Gen[Double], x: Gen[Double] = 0, y: Gen[Double] = 0) extends LocalTransform {
    def radian(tc: Seq[Int]): Double = degree.get(tc) * (Math.PI / 180)
    override def pivotPoint(tc: Seq[Int]): Point = Point(x.get(tc), y.get(tc))
    override def next: Transformation = Rotation(degree.next, x.next, y.next)
    override def exec(p: Point, tc: Seq[Int]): Point = rotate(p, -1 * radian(tc))
    override def execReverse(p: Point, tc: Seq[Int]): Point = rotate(p, radian(tc))

    def rotate(p: Point, r: Double): Point =
      Point(
        p.x * Math.cos(r) - p.y * Math.sin(r),
        p.x * Math.sin(r) + p.y * Math.cos(r)
      )
  }

}
