package snic

trait Completer {
  def completions(line: String, start: Int, end: Int): Iterable[String]

  private[snic] final def register(): Unit = {
    // todo implement
  }
}
