package sline.jline

import java.util.regex.Pattern

import sline.Highlighter

import org.jline.reader.LineReader
import org.jline.utils.AttributedString

class HighlighterDelegate(highlighterImpl: Highlighter)
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
