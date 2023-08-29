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
class CliImpl(
    completer: Option[Completer],
    highlighter: Option[Highlighter],
    hinter: Option[Hinter],
) extends Cli {
  val reader = {
    val builder = LineReaderBuilder.builder()
    completer.foreach { completer =>
      builder.completer(new CliImpl.CompleterDelegate(completer))
    }
    highlighter.foreach { highlighter =>
      builder.highlighter(new CliImpl.HighlighterDelegate(highlighter))
    }
    builder.build()
  }

  hinter.foreach { hinter =>
    new CliImpl.HinterWidgets(hinter, reader)
    // TODO enable
  }

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

object CliImpl {
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

  class HinterWidgets(hinter: Hinter, reader: LineReader)
      extends Widgets(reader) {
    val ForwardCharWidget = "_hinter-forward-char"
    val ForwardEndOfLineWidget = "_hinter-end-of-line"
    val ForwardWordWidget = "_hinter-forward-word"

    // TODO implement
  }
}
