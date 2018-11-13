package BildPackage

object Fillings {

  case class SolidSurface(gColor: Gen[Color]) extends Filling {

    val color: Color = gColor.get

    override def next: SolidSurface = SolidSurface(gColor.next)
    override def trace(p: Point): Color = color

  }

}
