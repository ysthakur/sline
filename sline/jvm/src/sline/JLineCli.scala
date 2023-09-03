package sline

import java.util.regex.Pattern

import org.jline.reader.{
  Candidate,
  EndOfFileException,
  LineReader,
  LineReaderBuilder,
  ParsedLine,
  UserInterruptException,
}
import org.jline.utils.AttributedString
import org.jline.widget.AutosuggestionWidgets
import org.jline.widget.Widgets

/** JVM implementation of the CLI using JLine */
class JLineCli(val reader: LineReader) extends Cli {

  override def readLine(prompt: String): Option[String] =
    try {
      Some(reader.readLine(prompt))
    } catch {
      case _: EndOfFileException =>
        None
      case _: UserInterruptException =>
        None
    }
}

object JLineCli {
  def apply(
      completer: Option[Completer],
      highlighter: Option[Highlighter],
      hinter: Option[Hinter],
  ): JLineCli = {
    val builder = LineReaderBuilder.builder()
    completer.foreach { completer =>
      builder.completer(new JLineCli.CompleterDelegate(completer))
    }
    highlighter.foreach { highlighter =>
      builder.highlighter(new JLineCli.HighlighterDelegate(highlighter))
    }
    val reader = builder.build()
    hinter.foreach { hinter =>
      new JLineCli.HinterWidgets(hinter, reader).enable()
    }

    new JLineCli(reader)
  }

  private class CompleterDelegate(completerImpl: Completer)
      extends org.jline.reader.Completer {
    override def complete(
        reader: LineReader,
        line: ParsedLine,
        candidates: java.util.List[Candidate],
    ): Unit =
      completerImpl
        .complete(line.line())
        .foreach { completion =>
          candidates.add(new Candidate(completion))
        }
  }

  private class HighlighterDelegate(highlighterImpl: Highlighter)
      extends org.jline.reader.Highlighter {
    override def highlight(
        reader: LineReader,
        buffer: String,
    ): AttributedString = {
      AttributedString.fromAnsi(highlighterImpl.highlight(buffer).render)
    }

    override def setErrorIndex(errorIndex: Int): Unit = ???

    override def setErrorPattern(errorPattern: Pattern): Unit = ???
  }

  /** Large chunks of this were shamelessly copied from JLine's
    * AutosuggestionWidgets.java
    */
  class HinterWidgets(hinter: Hinter, reader: LineReader)
      extends Widgets(reader) {
    import HinterWidgets.{EndOfLineWidget, ForwardCharWidget, ForwardWordWidget}

    private var enabled = false

    this
      .addWidget(ForwardCharWidget, () => this.accept(LineReader.FORWARD_CHAR))
    this.addWidget(EndOfLineWidget, () => this.accept(LineReader.END_OF_LINE))
    this
      .addWidget(ForwardWordWidget, () => this.accept(LineReader.FORWARD_WORD))
    this.addWidget(
      Widgets.AUTOSUGGEST_TOGGLE,
      () => {
        if (this.enabled)
          this.disable()
        else
          this.enable()
        this.enabled
      },
    )

    def enable(): Unit = {
      if (!this.enabled) {
        this.aliasWidget(ForwardCharWidget, LineReader.FORWARD_CHAR)
        this.aliasWidget(EndOfLineWidget, LineReader.END_OF_LINE)
        this.aliasWidget(ForwardWordWidget, LineReader.FORWARD_WORD)
        this.setSuggestionType(LineReader.SuggestionType.TAIL_TIP)

        this.enabled = true
      }
    }

    def disable(): Unit = {
      if (this.enabled) {
        this.aliasWidget("." + LineReader.FORWARD_CHAR, LineReader.FORWARD_CHAR)
        this.aliasWidget("." + LineReader.END_OF_LINE, LineReader.END_OF_LINE)
        this.aliasWidget("." + LineReader.FORWARD_WORD, LineReader.FORWARD_WORD)
        this.setSuggestionType(LineReader.SuggestionType.NONE)

        this.enabled = false
      }
    }

    override def tailTip(): String = {
      hinter.hint(this.buffer().toString()).getOrElse("")
    }

    private def accept(widget: String): Boolean = {
      val buffer = this.buffer()
      if (buffer.cursor() == buffer.length()) {
        this.putString(this.tailTip())
      } else {
        this.callWidget(widget)
      }
      true
    }
  }

  object HinterWidgets {
    private val ForwardCharWidget = "_autosuggest-forward-char"
    private val EndOfLineWidget = "_autosuggest-end-of-line"
    private val ForwardWordWidget = "_autosuggest-forward-word"
  }
}
