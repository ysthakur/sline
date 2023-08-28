package sline

trait Highlighter {
  def highlight(line: String): fansi.Str
}

object Highlighter {
  object Default extends Highlighter {
    override def highlight(line: String) = fansi.Str(line)
  }

  class Words(strings: Seq[String], highlightWord: String => fansi.Attr)
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
