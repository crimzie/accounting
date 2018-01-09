package services

import java.io.{File, FileOutputStream, PrintWriter}

import monix.eval.Task

import scala.io.Source
import scala.language.postfixOps
import scala.util.Try

trait Storage {
  def append(transaction: Long): Task[Unit]

  def list(): Task[List[Long]]

  def restore(): Long
}

/**
  * Simple implementation in place of a more reliable solution, for example
  * with a database.
  */
class DiskStorage(file: File) extends Storage {
  override def append(transaction: Long): Task[Unit] = Task {
    val p = new PrintWriter(new FileOutputStream(file, true))
    val w = Try { p append transaction.toString + "\n"}
    p.close()
    if (w.isFailure) throw w.failed.get
  }
                                     
  override def list(): Task[List[Long]] =
    Task { Source.fromFile(file).getLines().filter(_.nonEmpty).map(_.toLong).toList.reverse }

  override def restore(): Long =
    Source.fromFile(file).getLines().filter(_.nonEmpty).map(_.toLong).sum
}
