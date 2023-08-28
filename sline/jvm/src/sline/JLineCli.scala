package sline

import org.jline.reader.{Candidate, LineReader, LineReaderBuilder, ParsedLine}

class JLineCli(private val completer: Completer) extends Cli {
  val reader = LineReaderBuilder
    .builder()
    .completer(new JLineCli.JLineCompleter(completer))
    .build()

  override def readLine(prompt: String): String = reader.readLine(prompt)
}

object JLineCli {
  private class JLineCompleter(completerImpl: Completer)
      extends org.jline.reader.Completer {
    override def complete(
        reader: LineReader,
        line: ParsedLine,
        candidates: java.util.List[Candidate],
    ): Unit =
      completerImpl
        .complete(line.line())
        .foreach { completion =>
          candidates.add(Candidate(completion))
        }
  }
}
