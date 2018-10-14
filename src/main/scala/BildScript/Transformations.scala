package BildScript

object Transformations {

  case class PositionTransform(x: Gen[Double], y: Gen[Double]) extends Transformation {
    val mp = Point(x, y)
    override def next: Transformation = PositionTransform(x.nextGen, y.nextGen)
    override def exec(p: Point): Unit = p addMutate mp
  }

}
