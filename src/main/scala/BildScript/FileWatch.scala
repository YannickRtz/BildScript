package BildScript

import java.io.File
import java.nio.file.{Files, Paths}

import com.barbarysoftware.watchservice.StandardWatchEventKind._
import com.barbarysoftware.watchservice.{WatchEvent, WatchService, WatchableFile}

import scala.collection.JavaConverters
import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

object FileWatch extends App {

  // https://www.baeldung.com/java-nio2-watchservice
  // https://stackoverflow.com/questions/1183645/eval-in-scala
  // https://github.com/gjoseph/BarbaryWatchService/blob/master/Demo.asciidoc

  val watchService = WatchService.newWatchService
  val currentDir = new WatchableFile(new File("./src/main/scala/WatchMe"))
  currentDir.register(watchService, ENTRY_MODIFY)

  val runnable: Runnable = createRunnable(watchService)
  val consumer: Thread = new Thread(runnable)
  consumer.start()
  Thread.sleep(Long.MaxValue)
  consumer.interrupt()

  private def createRunnable(watcher: WatchService): Runnable = new Runnable() {
    override def run(): Unit = {
      System.out.println("Watching for file changes in WatchMe folder")
      val toolbox = currentMirror.mkToolBox()
      var break = false
      while(!break) { // wait for key to be signaled
        try {
          val key = watcher.take
          key.pollEvents.forEach { event: WatchEvent[_] =>
            val kind: WatchEvent.Kind[_] = event.kind
            if (!kind.eq(OVERFLOW) && event.context.toString.endsWith(".scala")) {
              // The filename is the context of the event.
              val ev = event.asInstanceOf[WatchEvent[File]]
              println("Event kind: " + kind + ". File affected: ..." + ev.context.toString.takeRight(20))
              println("Preparing code...")
              val allLines = Files.readAllLines(Paths.get(event.context.toString))
              val filtered = JavaConverters.asScalaBuffer(allLines).toList.filterNot { str =>
                str.trim.startsWith("package") || str.trim.isEmpty
              }.dropRight(1)
              val patched = filtered.updated(
                filtered.indexWhere(_.trim.startsWith("object")),
                "println(\"Initializing objects...\")"
              )
              // drop last closing curly brace
              val str = patched.mkString("\n")
              println("Parsing code...")
              val code = toolbox.parse(str)
              println("Starting sketch...")
              toolbox.eval(code)
            }
          }
          // Reset the key -- this step is critical to receive further watch events.
          if (!key.reset)
            break = true
        } catch {
          case _: InterruptedException => return
        }
      }
    }
  }

}
