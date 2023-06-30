package snic.highlight

import scala.io.AnsiColor
import scala.util.matching.Regex

case class FansiRegexHighlighter(colors: Iterable[(Regex, fansi.Attrs)])
    extends Highlighter {
  override def highlight(line: String): String = {
    val plaintext = fansi.Str.Strip(line).plainText
    System.out.flush()
    val overlays =
      for {
        (regex, attrs) <- colors
        m <- regex.findAllMatchIn(plaintext)
      } yield (attrs, m.start, m.end)
    fansi.Str(plaintext).overlayAll(overlays.toSeq).render
  }
}
