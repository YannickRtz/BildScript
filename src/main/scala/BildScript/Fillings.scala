package BildScript

object Fillings {

  case class SolidSurface(color: Color) extends Filling {

    // println("new filling")

    // TODO: Enable random colors
    override def next: SolidSurface = {
      SolidSurface(color)
    }
    override def trace(p: Point): Color = color

  }

}
