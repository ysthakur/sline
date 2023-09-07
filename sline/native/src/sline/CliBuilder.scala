package sline

import java.nio.file.Path

import sline.replxx.{replxx_init, Replxx}

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

  override def defaultHistory(file: Option[Path]) = {
    this.history = Some(new ReplxxFileHistory(repl, file))
    this
  }

  override def build(): Cli =
    new ReplxxCli(repl, completer, highlighter, hinter, history)
}
