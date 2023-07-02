package snic

import scala.util.matching.Regex

trait Highlighter {
  /** @return A string highlighted using ANSI escapes */
  def highlight(line: String): Iterable[HighlightRange]
}

/** @param start
  *   The index of the first character to highlight
  * @param end
  *   The index to stop highlighting at (exclusive)
  * @param ansi
  *   The ANSI code(s) used to highlight
  */
case class HighlightRange(start: Int, end: Int, ansi: String)

object DummyHighlighter extends Highlighter {
  override def highlight(line: String) = Nil
}

class RegexHighlighter(colors: Iterable[(Regex, String)]) extends Highlighter {
  override def highlight(line: String) = for {
    (regex, ansi) <- colors
    m <- regex.findAllMatchIn(line)
  } yield HighlightRange(m.start, m.end, ansi)
}
