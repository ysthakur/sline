package snic.highlight

import scala.util.matching.Regex

case class FansiRegexHighlighter(colors: Iterable[(Regex, fansi.Attrs)])
    extends Highlighter {
  override def highlight(line: String): String = {
    val sanitized = fansi.Str.Sanitize(line).plainText
    val overlays =
      for {
        (regex, attrs) <- colors
        m <- regex.findAllMatchIn(sanitized)
      } yield {
        (attrs, m.start, m.end)
      }
    fansi.Str(sanitized).overlayAll(overlays.toSeq).render
  }
}
