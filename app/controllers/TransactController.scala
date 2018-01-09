package controllers

import play.api.mvc._

class TransactController(implicit cc: ControllerComponents) extends AbstractController(cc) {

  def credit(): Action[String] = Action.async(parse text 20) { req => ??? }

  def debit(): Action[String] = Action.async(parse text 20) { req => ??? }

  def history(n: Int, page: Int): Action[AnyContent] = Action.async { req => ??? }
}
