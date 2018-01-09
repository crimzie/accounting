import controllers.TransactController
import monix.execution.Scheduler
import play.api._
import play.api.mvc.{ControllerComponents, EssentialFilter}
import play.api.routing.Router
import play.api.Mode.{Dev, Prod, Test}
import services.{Balance, DiskStorage, MVarBalance, Storage}

/** This partially replaces the default application loader that uses Guice with
  * a simpler one using implicits-based compile-time DI, so that wiring can be
  * type-checked by compiler.
  */
class LoaderImpl extends ApplicationLoader {
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

  private implicit val sch: Scheduler = Scheduler(executionContext)
  private implicit lazy val cc: ControllerComponents = controllerComponents

  private implicit val st: Storage = new DiskStorage(conf.history)
  private implicit val bl: Balance = new MVarBalance(st.restore())
  private implicit val tc: TransactController = new TransactController

  override lazy val router: Router = new RouterImpl
  override lazy val httpFilters: Seq[EssentialFilter] = Nil
}
