package snic.highlight

import scala.util.matching.Regex

case class FansiRegexHighlighter(colors: Iterable[(Regex, fansi.Attrs)])
    extends Highlighter {
  override def highlight(line: String): String = {
    val plaintext = fansi.Str(line).plainText
    System.out.flush()
    val overlays =
      for {
        (regex, attrs) <- colors
        m <- regex.findAllMatchIn(plaintext)
      } yield (attrs, m.start, m.end)
    val rendered = fansi.Str(plaintext).overlayAll(overlays.toSeq).render
    rendered
    // readline needs invisible characters to be wrapped in \001 and \002
    // fansi
    //   .Str
    //   .ansiRegex
    //   .pattern()
    //   .r
    //   .replaceAllIn(rendered, m => s"\u0001${m.matched}\u0002")
  }
}
