package sline

object CliFactory {
  def create(completer: Completer) = new ReplxxCli(completer)
}
