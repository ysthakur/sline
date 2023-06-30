package snic

trait Completer {
  def completions(line: String, start: Int, end: Int): Iterable[String]

  final private[snic] def register(): Unit = {
    // todo implement
  }
}
