/** Programmatic config as a typesafe way to replace Lightbend Config.
  */
case class Conf()

object Conf {
  val devConf: Conf = Conf()
  val testConf: Conf = devConf
  val prodConf: Conf = Conf()
}
