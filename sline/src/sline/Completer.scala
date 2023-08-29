package sline

/** Provides completions that the user can tab through */
trait Completer {
  /** Give a list of possible completions for the current line */
  def complete(line: String): Iterable[String]
}

object Completer {
  /** Complete based on a list of strings */
  class Strings(strings: Seq[String]) extends Completer {
    override def complete(line: String): Iterable[String] =
      strings.filter(_.startsWith(line))
  }
}
