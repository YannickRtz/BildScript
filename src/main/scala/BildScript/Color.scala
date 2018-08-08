package BildScript

case class Color(red: Float, green: Float, blue: Float, alpha: Float) {
  def overlay(c: Color): Color = {
    if (c.alpha == 0) this
    else {
      val a = c.alpha
      val a1 = 1 - a
      // TODO: Correct alpha handling
      Color(red * a1 + c.red * a,
        green * a1 + c.green * a,
        blue * a1 + c.blue * a,
        Seq(alpha + a, 1).min)
    }
  }
}

object Color {
  val WHITE = new Color(1,1,1,1)
  val CLEAR = new Color(0, 0, 0, 0)
}