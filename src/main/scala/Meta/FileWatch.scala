package Meta

import java.io.File
import java.nio.file.{Files, Paths}

import com.barbarysoftware.watchservice.StandardWatchEventKind._
import com.barbarysoftware.watchservice.{WatchEvent, WatchKey, WatchService, WatchableFile}

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
  println("Watching for file changes in WatchMe folder")
  val toolbox = currentMirror.mkToolBox()

  var key: WatchKey = watchService.take
  while (key != null) {
    key.pollEvents.forEach { event: WatchEvent[_] =>
      if (!event.kind.eq(OVERFLOW) && event.context.toString.endsWith(".scala")) {
        // The filename is the context of the event.
        println("Event kind: " + event.kind + ". File affected: ..." + event.context.toString.takeRight(20))
        print("Preparing and parsing code...")
        Measurement.start()
        val allLines = Files.readAllLines(Paths.get(event.context.toString))
        val filtered = JavaConverters.asScalaBuffer(allLines).toList.filterNot { str =>
          str.trim.startsWith("package") || str.trim.isEmpty
        }.dropRight(1) // drop last closing curly brace
        val patched = filtered.updated(
          filtered.indexWhere(_.trim.startsWith("object")),
          "Measurement.take(); print(\"Initializing objects...\");"
        )
        val str = patched.mkString("\n")
        val code = toolbox.parse(str)
        Measurement.take()
        print("Starting sketch...")
        toolbox.eval(code)
      }
    }
    key.reset
    key = watchService.take
  }

  println("Stopped watching.")

}
