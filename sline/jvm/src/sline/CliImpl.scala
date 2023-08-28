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

/** JVM implementation of the CLI using JLine */
class CliImpl(
    private val completer: Completer,
    private val highlighter: Highlighter,
) extends Cli {
  val reader = LineReaderBuilder
    .builder()
    .completer(new CliImpl.CompleterDelegate(completer))
    .highlighter(new CliImpl.HighlighterDelegate(highlighter))
    .build()

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
}
