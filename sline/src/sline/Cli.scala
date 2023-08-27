package sline

trait Cli {
  /** Set the prompt asynchronously while [[readLine]] is still executing */
  def setPrompt(prompt: String): Unit

  def readLine(prompt: String): String

  def close(): Unit
}

object Cli {
  def apply(completer: Completer = Completer.Default): Cli =
    CliFactory.create(completer)
}
