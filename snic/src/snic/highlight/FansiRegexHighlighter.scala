package snic.highlight

import scala.util.matching.Regex

case class FansiRegexHighlighter(colors: Iterable[(Regex, fansi.Attrs)])
    extends Highlighter {
  override def highlight(line: String): String = {
    val overlays =
      for {
        (regex, attrs) <- colors
        m <- regex.findAllMatchIn(line)
      } yield {
        (attrs, m.start, m.end)
      }
    fansi.Str(line).overlayAll(overlays.toSeq).render
  }
}
