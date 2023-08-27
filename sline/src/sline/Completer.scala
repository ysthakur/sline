package sline

trait Completer {
  def complete(line: String): Iterable[String]
}

object Completer {
  object Default extends Completer {
    override def complete(line: String) = Nil
  }
}
