package BildScript

import java.nio.file._

import com.sun.nio.file.SensitivityWatchEventModifier

import scala.collection.JavaConverters
import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

object EvalTest extends App {

  // https://www.baeldung.com/java-nio2-watchservice

  val watchService = FileSystems.getDefault.newWatchService

  val currentDir = Paths.get("./src/main/scala/WatchMe")
  println(currentDir.toAbsolutePath)

  // currentDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)

  currentDir.register(watchService,
    Array[WatchEvent.Kind[_]](StandardWatchEventKinds.ENTRY_MODIFY),
    SensitivityWatchEventModifier.HIGH)

  val toolbox = currentMirror.mkToolBox()

  var key: WatchKey = watchService.take()

  while (key != null) {
    key.pollEvents.forEach { event: WatchEvent[_] =>
      println("Event kind:" + event.kind + ". File affected: " + event.context + ".")
      println("Compilation...")
      val allLines = Files.readAllLines(Paths.get("./src/main/scala/WatchMe/" + event.context))
      val filtered = JavaConverters.asScalaBuffer(allLines).toList.filterNot { str =>
        str.trim.startsWith("package") ||
        str.trim.startsWith("object") ||
        str.trim.isEmpty
      }.dropRight(1)
      // drop last closing curly brace
      val str = filtered.mkString("\n")
      println("Starting sketch...")
      toolbox.eval(toolbox.parse(str))
    }
    key.reset
    key = watchService.take()
  }

  // https://stackoverflow.com/questions/1183645/eval-in-scala

}
