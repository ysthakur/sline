import sline.{Cli, Completer, Highlighter, Hinter}

object Demo {
  def main(args: Array[String]): Unit = {
    val keywords = Seq("foobar", "bar", "baz")
    val cli = Cli(
      completer = new Completer.Strings(keywords),
      highlighter =
        new Highlighter.Words(
          keywords,
          {
            case keyword if keywords.contains(keyword) =>
              fansi.Color.Blue
            case _ =>
              fansi.Color.Reset
          },
        ),
      hinter =
        new Hinter {
          override def color = fansi.Color.LightGray

          override def hint(line: String): Option[String] = {
            keywords
              .filter(_.startsWith(line))
              .maxByOption(_.length)
              .map(_.substring(line.length))
          }
        },
    )

    for (line <- cli.lines("> ")) {
      println(s"Read '$line'")
    }
  }
}
