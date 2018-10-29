package BildScript

case class Color(var red: Double, var green: Double, var blue: Double, var alpha: Double) {

  // println("new color")

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

  def overlayCopy(c: Color): Color = {
    if (c.alpha == 0) this
    else {
      val a = c.alpha
      val a1 = 1 - a
      // TODO: Correct alpha handling
      red = red * a1 + c.red * a
      green = green * a1 + c.green * a
      blue = blue * a1 + c.blue * a
      alpha = Seq(alpha + a, 1).min
      this
    }
  }

  def toARGB: ARGB = {
    val alpha255 = (255 * alpha).toInt
    val red255 = (255 * red).toInt
    val green255 = (255 * green).toInt
    val blue255 = (255 * blue).toInt
    var color: ARGB = 0
    color |= alpha255 << 24
    color |= red255 << 16
    color |= green255 << 8
    color | blue255
  }

}

object Color {
  def WHITE = new Color(1,1,1,1)
  def CLEAR = new Color(0, 0, 0, 0)
}