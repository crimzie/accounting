import controllers.TransactController
import play.api._
import play.api.mvc.{ControllerComponents, EssentialFilter}
import play.api.routing.Router
import play.api.Mode.{Dev, Prod, Test}

/** This partially replaces the default application loader that uses Guice with
  * a simpler one using implicits-based compile-time DI, so that wiring can be
  * type-checked by compiler.
  */
class AppLoaderImpl extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application = {
    LoggerConfigurator(context.environment.classLoader) foreach (_ configure context.environment)
    new Components(context).application
  }
}

class Components(context: ApplicationLoader.Context) extends BuiltInComponentsFromContext(context) {
  implicit val conf: Conf = context.environment.mode match {
    case Dev  => Conf.devConf
    case Test => Conf.testConf
    case Prod => Conf.prodConf
  }
  Logger.info(s"Starting with ${context.environment.mode} configuration.")

  private implicit lazy val cc: ControllerComponents = controllerComponents

  private implicit val tc: TransactController = new TransactController

  override lazy val router: Router = new RouterImpl
  override lazy val httpFilters: Seq[EssentialFilter] = Nil
}
