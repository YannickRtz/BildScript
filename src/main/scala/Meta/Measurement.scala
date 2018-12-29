package Meta

object Measurement {
  private var t0: Long = System.nanoTime()
  private var overall0: Long = System.nanoTime()

  def start(): Unit = {
    t0 = System.nanoTime()
    overall0 = t0
  }

  def take(): Unit = {
    val t1 = System.nanoTime()
    val elapsed = t1 - t0
    t0 = t1
    val ms = elapsed / 1000000.0
    println(" Took " + ms + "ms")
  }

  def stop(): Unit = {
    val overall1 = System.nanoTime()
    val elapsed = overall1 - overall0
    val seconds = elapsed / 1000000000.0
    println("Overall: " + seconds + " seconds")
  }
}
