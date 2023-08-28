package sline

private[sline] object CliFactory {
  def create(completer: Completer) = new JLineCli(completer)
}
