package sline

trait Cli {
  def readLine(prompt: String): String

  def close(): Unit = {}
}

object Cli {
  def apply(completer: Completer = Completer.Default): Cli =
    CliFactory.create(completer)
}
