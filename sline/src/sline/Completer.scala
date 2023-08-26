package sline

trait Completer {
  def complete(line: String): Iterable[String]
}
