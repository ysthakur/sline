package snic.highlight

trait Highlighter {
  /** @return A string highlighted using ANSI escapes */
  def highlight(line: String): String
}

object DummyHighlighter extends Highlighter {
  override def highlight(line: String) = line
}
