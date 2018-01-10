package controllers

import java.io.File

import monix.execution.schedulers.TestScheduler
import org.scalatestplus.play._
import play.api.mvc.ControllerComponents
import play.api.test._
import play.api.test.Helpers._
import services.{Balance, DiskStorage, MVarBalance, Storage}

import scala.concurrent.duration._
import scala.language.postfixOps

class TransactControllerSpec extends PlaySpec {
  implicit val components: ControllerComponents = stubControllerComponents()
  implicit val tscheduler: TestScheduler = TestScheduler()

  "TransactController debit" should {

    "properly handle race conditions" in {
      implicit val balance: Balance = new MVarBalance(0)
      implicit val storage: Storage = new DiskStorage(new File("target/testst"))
      val controller = new TransactController
      val req = FakeRequest(POST, "/api/debit", FakeHeaders(CONTENT_TYPE -> TEXT :: Nil), "15")

      controller.debit()(req)
      controller.debit()(req)
      controller.debit()(req)
      controller.debit()(req)
      controller.debit()(req)
      controller.debit()(req)
      controller.debit()(req)
      controller.debit()(req)
      controller.debit()(req)
      controller.debit()(req)

      tscheduler.tick(1 second)

      val fBal = balance.take().runAsync
      tscheduler.tick(1 second)
      fBal.value.get.get mustBe 150
    }
  }
}
