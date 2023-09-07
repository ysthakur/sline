package sline

import replxx.replxx_init

class CliBuilder {
  private val repl = replxx_init()
  private var completer: Option[Completer] = None
  private var highlighter: Option[Highlighter] = None
  private var hinter: Option[Hinter] = None
  private var history: Option[History] = None

  def withCompleter(completer: Completer): CliBuilder = {
    this.completer = Some(completer)
    this
  }

  def withHighlighter(highlighter: Highlighter): CliBuilder = {
    this.highlighter = Some(highlighter)
    this
  }

  def withHinter(hinter: Hinter): CliBuilder = {
    this.hinter = Some(hinter)
    this
  }

  def withHistory(history: History): CliBuilder = {
    this.history = Some(history)
    this
  }

  def withFileHistory(filename: String): CliBuilder = {
    this.history = Some(new FileHistory(repl, filename))
    this
  }

  def build(): Cli =
    new ReplxxCli(repl, completer, highlighter, hinter, history)
}
