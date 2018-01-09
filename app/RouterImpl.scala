import controllers.TransactController
import play.api.routing.SimpleRouter
import play.api.routing.Router.Routes
import play.api.routing.sird._

/** A programmatic routing setup with Play's routing DSL that's actually done
  * with plain Scala rather than the default Play's router.
  */
class RouterImpl(implicit tc: TransactController) extends SimpleRouter {
  override val routes: Routes = {
    case POST(p"/api/credit")                                           => tc.credit()
    case POST(p"/api/debit")                                            => tc.debit()
    case GET(p"/api/history" ? q"n=${int(n)}" & q_o"page=${int(page)}") => tc.history(n, page)
  }
}
