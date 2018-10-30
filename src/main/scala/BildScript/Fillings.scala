package BildScript

object Fillings {

  case class SolidSurface(gColor: GColor) extends Filling {

    // println("new filling")

    val color: Color = gColor.get

    override def next: SolidSurface = SolidSurface(gColor.next)
    override def trace(p: Point): Color = color

  }

}
