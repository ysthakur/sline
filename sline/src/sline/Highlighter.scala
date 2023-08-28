package sline

trait Highlighter {
  def highlight(line: String): fansi.Str
}

object Highlighter {
  object Default extends Highlighter {
    override def highlight(line: String) = fansi.Str(line)
  }

  class Strings(strings: Seq[String]) extends Highlighter {
    override def highlight(line: String): fansi.Str = {
      fansi
        .Str
        .join(
          line
            .split(" ")
            .map { word =>
              if (strings.contains(word))
                fansi.Color.Blue(word)
              else
                fansi.Color.Red(word)
            },
          sep = fansi.Str(" "),
        )
    }
  }
}
