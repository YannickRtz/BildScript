package BildPackage

object Fillings {

  case class SolidSurface(gColor: Gen[Color]) extends Filling {
    override def next: SolidSurface = SolidSurface(gColor.next)
    override def trace(p: Point): Color = gColor.get
    override def walk(tc: Seq[Int]): Unit = gColor.walk(tc)
  }

}
