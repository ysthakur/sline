package sline

import sline.replxx.replxx.{replxx_init, Replxx}
import sline.replxx.ReplxxBuiltinHistory
import sline.replxx.ReplxxCli

class CliBuilder extends AbstractCliBuilder {
  private val repl: Replxx = replxx_init()

  private var completer: Option[Completer] = None
  private var highlighter: Option[Highlighter] = None
  private var hinter: Option[Hinter] = None
  private var history: Option[History] = None

  override def completer(completer: Completer) = {
    this.completer = Some(completer)
    this
  }

  override def highlighter(highlighter: Highlighter) = {
    this.highlighter = Some(highlighter)
    this
  }

  override def hinter(hinter: Hinter) = {
    this.hinter = Some(hinter)
    this
  }

  override def history(history: History) = {
    this.history = Some(history)
    this
  }

  override def defaultHistory() = {
    this.history = Some(new ReplxxBuiltinHistory(repl))
    this
  }

  override def build(): Cli =
    new ReplxxCli(repl, completer, highlighter, hinter, history)
}
