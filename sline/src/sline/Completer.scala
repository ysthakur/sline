package sline

trait Completer {
  def complete(line: String): Iterable[String]
}

object Completer {
  class Strings(strings: Seq[String]) extends Completer {
    override def complete(line: String): Iterable[String] =
      strings.filter(_.startsWith(line))
  }
}
