package services

import monix.eval.{MVar, Task}

trait Balance {
  def take(): Task[Long]

  def put(b: Long): Task[Unit]
}

class MVarBalance(initial: Long) extends Balance {
  private val balance: MVar[Long] = MVar(initial)

  override def take(): Task[Long] = balance.take

  override def put(b: Long): Task[Unit] = balance.put(b)
}
