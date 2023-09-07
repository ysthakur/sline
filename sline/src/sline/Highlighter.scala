package sline

trait Highlighter {
  /** Highlight the current line */
  def highlight(line: String): fansi.Str
}

object Highlighter {
  /** A highlighter that splits the line up into words (space-separated), and
    * applies the given function to each word to determine what color to
    * highlight it.
    */
  class Words(strings: Seq[String])(highlightWord: String => fansi.Attrs)
      extends Highlighter {
    override def highlight(line: String): fansi.Str = {
      // TODO don't use regex, this makes fansi reparse the resulting string
      fansi.Str(
        raw"\b(\w+)\b"
          .r
          .replaceAllIn(
            line,
            m => {
              val word = m.group(1)
              highlightWord(word)(word).render
            },
          )
      )
    }
  }
}
