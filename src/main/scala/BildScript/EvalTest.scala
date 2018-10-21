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
      val filtered = JavaConverters.asScalaBuffer(allLines).toList.filter { str =>
        if (str.contains("package")) false
        else if (str.contains("import")) true
        else !str.contains("}") && !str.contains("{")
      }
      val str = filtered.mkString("\n")
      println("Starting sketch...")
      toolbox.eval(toolbox.parse(str))
    }
    key.reset
    key = watchService.take()
  }

  // https://stackoverflow.com/questions/1183645/eval-in-scala

  // val toolbox = currentMirror.mkToolBox()

  /*val as =
    """
      |import BildScript._
      |import BildScript.Fillings.SolidSurface
      |import BildScript.Generators.{CountGen, EvoGen}
      |import BildScript.Masks.RectMask
      |import BildScript.Transformations.PositionTransform
      |
      |import scala.language.{implicitConversions, postfixOps}
      |
      |object Playground extends App {
      |
      |val hey = "ho"
      |
      |  Bild (
      |    SolidSurface(Color(0,1,1,1)) +
      |
      |    Bild (
      |      PositionTransform(0, 0) +
      |
      |      Bild (
      |        SolidSurface(Color(0,1,0,0.5f)) +
      |        RectMask(0.2, 0.2) +
      |        PositionTransform(EvoGen(_%7*0.4), EvoGen(x=>Math.floor(x/7)*0.4))
      |        //PositionTransform(EvoGen(_%7*0.4), EvoGen(x=>(x/7).floor*0.4)) +
      |      ) * 4
      |
      |    ) * 3
      |
      |  )
      |    .raster(Resolution(256, 512), 2)
      |    .output("image.png")
      |
      |}
    """.stripMargin

  val compe = toolbox.eval(toolbox.parse(as))

  println(compe.getClass)*/
  //println(compe) // prints 10

}
