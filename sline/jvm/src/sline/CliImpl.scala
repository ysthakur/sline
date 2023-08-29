package sline

/** This is simply to allow users to call [[Cli.apply]] to get a JLine-backed
  * implementation of [[Cli]] for the JVM See [[Cli.apply]] for more information
  * on why this exists
  */
object CliImpl {
  def apply(
      completer: Option[Completer],
      highlighter: Option[Highlighter],
      hinter: Option[Hinter],
  ): Cli = JLineCli(completer, highlighter, hinter)
}
