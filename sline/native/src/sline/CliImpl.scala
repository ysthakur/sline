package sline

/** This is simply to allow users to call [[Cli.apply]] to get a Replxx-backed
  * implementation of [[Cli]] for Scala Native. See [[Cli.apply]] for more
  * information on why this exists
  */
object CliImpl {
  def apply(
      completer: Option[Completer],
      highlighter: Option[Highlighter],
      hinter: Option[Hinter],
      // history: Option[ReplxxCli => History],
  ): Cli = new ReplxxCli(completer, highlighter, hinter)

  def fileHistory(filename: String): ReplxxCli => History =
    cli => new FileHistory(cli.repl, filename)
}
