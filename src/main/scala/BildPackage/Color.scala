package BildPackage

case class Color(var red: Double, var green: Double, var blue: Double, var alpha: Double = 1) {

  red = Math.max(0, Math.min(1, red))
  green = Math.max(0, Math.min(1, green))
  blue = Math.max(0, Math.min(1, blue))
  alpha = Math.max(0, Math.min(1, alpha))

  def overlayMutate(c: Color): Unit = {
    // Algorithm from https://de.wikipedia.org/wiki/Alpha_Blending
    if (c.alpha > 0) {
      val a = c.alpha
      val a1 = 1 - a
      alpha = a + a1 * alpha  // mutation!
      val a2 = 1 / alpha
      red = a2 * (a * c.red + a1 * alpha * red)
      green = a2 * (a * c.green + a1 * alpha * green)
      blue = a2 * (a * c.blue + a1 * alpha * blue)
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
  def WHITE = Color(1, 1, 1)
  def CLEAR = Color(0, 0, 0, 0)
  def RED = Color(1, 0, 0)

  def fromARGB(a: Int): Color = {
    val alpha = ((a >> 24) & 0xFF) / 255.0
    val red = ((a >> 16) & 0xFF) / 255.0
    val green = ((a >> 8) & 0xFF) / 255.0
    val blue = (a & 0xFF) / 255.0
    Color(red, green, blue, alpha)
  }
}