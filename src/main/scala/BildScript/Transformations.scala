package BildScript

object Transformations {

  case class PositionTransform(x: Gen[Float], y: Gen[Float]) extends Transformation {
    override def next: Transformation = PositionTransform(x.nextGen, y.nextGen)
    override def exec(p: Point): Point = p - Point(x, y)
  }

}
