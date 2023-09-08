package sline

import jline.{
  CompleterDelegate,
  HighlighterDelegate,
  HinterWidgets,
  HistoryDelegate,
  JLineCli,
}
import org.jline.reader.impl.history.DefaultHistory
import org.jline.reader.LineReaderBuilder

class CliBuilder extends AbstractCliBuilder {
  private val builder = LineReaderBuilder.builder()
  private var hinter: Option[Hinter] = None

  override def completer(completer: Completer) = {
    builder.completer(new CompleterDelegate(completer))
    this
  }

  override def highlighter(highlighter: Highlighter) = {
    builder.highlighter(new HighlighterDelegate(highlighter))
    this
  }

  override def hinter(hinter: Hinter) = {
    this.hinter = Some(hinter)
    this
  }

  override def history(history: History) = {
    builder.history(new HistoryDelegate(history))
    this
  }

  override def defaultHistory() = {
    builder.history(new DefaultHistory())
    this
  }

  override def build(): Cli = {
    val reader = builder.build()
    hinter.foreach { hinter =>
      new HinterWidgets(hinter, reader).enable()
    }
    new JLineCli(reader)
  }
}
