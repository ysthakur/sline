package sline

import java.nio.file.Path

import org.jline.reader.impl.history.DefaultHistory
import org.jline.reader.LineReaderBuilder

class CliBuilder extends AbstractCliBuilder {
  private val builder = LineReaderBuilder.builder()
  private var hinter: Option[Hinter] = None

  override def completer(completer: Completer) = {
    builder.completer(new JLineCli.CompleterDelegate(completer))
    this
  }

  override def highlighter(highlighter: Highlighter) = {
    builder.highlighter(new JLineCli.HighlighterDelegate(highlighter))
    this
  }

  override def hinter(hinter: Hinter) = {
    this.hinter = Some(hinter)
    this
  }

  override def history(history: History) = {
    builder.history(new JLineCli.HistoryDelegate(history))
    this
  }

  override def defaultHistory(file: Option[Path]) = {
    val history = new DefaultHistory()
    file.foreach { filePath =>
      history.read(filePath, false)
    }
    builder.history(history)
    this
  }

  override def build(): Cli = {
    val reader = builder.build()
    hinter.foreach { hinter =>
      new JLineCli.HinterWidgets(hinter, reader).enable()
    }
    new JLineCli(reader)
  }
}
