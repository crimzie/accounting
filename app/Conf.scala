import java.io.File

/** Programmatic config as a typesafe way to replace Lightbend Config.
  */
case class Conf(history: File) {
  history.createNewFile
  assert(history.canRead && history.canWrite)
}

object Conf {
  lazy val devConf: Conf = Conf(history = new File("./balance"))
  lazy val testConf: Conf = devConf
  lazy val prodConf: Conf = Conf(history = new File("./balance"))
}
