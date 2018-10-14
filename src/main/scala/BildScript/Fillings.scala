package BildScript

object Fillings {

  case class SolidSurface(color: Color) extends Filling {

    println("new filling")

    // TODO: Enable random colors
    override def next: SolidSurface = {
      println("solid filling")
      SolidSurface(color)
    }
    override def sample(p: Point): Color = color

  }

}
