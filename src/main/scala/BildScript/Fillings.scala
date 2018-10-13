package BildScript

object Fillings {

  case class SolidSurface(color: Color) extends Filling {
    // TODO: Enable random colors
    override def next: SolidSurface = SolidSurface(color)
    override def sample(p: Point): Color = color
  }

}
