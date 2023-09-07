package sline.jline

import sline.Completer

import org.jline.reader.Candidate
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine

class CompleterDelegate(completerImpl: Completer)
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
