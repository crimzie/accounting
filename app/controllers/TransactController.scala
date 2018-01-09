package controllers

import monix.eval.Task
import monix.execution.Scheduler
import play.api.libs.json.{JsArray, JsNumber}
import play.api.mvc._
import services.{Balance, Storage}

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

import scala.concurrent.duration._

class TransactController(
    implicit cc: ControllerComponents,
    scheduler: Scheduler,
    balance: Balance,
    storage: Storage,
) extends AbstractController(cc) {

  private val br: Task[Result] = Task pure BadRequest("Invalid amount format.")

  def credit(): Action[String] = Action.async(parse text 20) { req =>
    val task = Try { req.body.toLong.ensuring(_ > 0) } match {
      case Failure(_) => br
      case Success(a) =>
        balance take () flatMap {
          case x if x < a => balance put x map (_ => Conflict(x.toString))
          case x =>
            val t = for {
              _ <- storage append -a timeout 5.seconds onErrorRestart 3

              y = x - a
              _ <- balance put y
            } yield Ok(y.toString)
            t onErrorFallbackTo balance.put(x).map(_ => InternalServerError)
        }
    }
    task.runAsync
  }

  def debit(): Action[String] = Action.async(parse text 20) { req =>
    val task = Try { req.body.toLong.ensuring(_ > 0) } match {
      case Failure(_) => br
      case Success(a) =>
        balance take () flatMap { x =>
          val t = for {
            _ <- storage append a timeout 5.seconds onErrorRestart 3
            y = x + a
            _ <- balance put y
          } yield Ok(y.toString)
          t onErrorFallbackTo balance.put(x).map(_ => InternalServerError)
        }
    }
    task.runAsync
  }

  def history(n: Int, page: Option[Int]): Action[AnyContent] = Action.async {
    storage list () map { l =>
      val s: Seq[List[Long]] = l.sliding(math.max(10, n)).toList
      val max = s.length - 1
      val i = page map (x => math.min(math.max(0, x), max)) getOrElse 0
      Ok(JsArray(s(i) map (JsNumber apply _)))
    } runAsync
  }
}
