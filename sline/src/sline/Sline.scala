package sline

case class Sline(
    val history: History,
    val highlighter: Highlighter,
    val completer: Completer,
    val backend: Backend,
) {
  def readLine(prompt: String): String = {
    backend.readLine(prompt)
  }

  def close(): Unit = {
    backend.close()
  }
}
