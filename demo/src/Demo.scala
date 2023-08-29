import sline.{Cli, Completer, Highlighter, Hinter}

object Demo {
  def main(args: Array[String]): Unit = {
    val keywords = Seq("foobar", "bar", "baz")
    val cli = Cli(
      completer = Some(new Completer.Strings(keywords)),
      highlighter = Some(
        new Highlighter.Words(
          keywords,
          {
            case keyword if keywords.contains(keyword) =>
              fansi.Color.Blue
            case _ =>
              fansi.Color.Reset
          },
        )
      ),
      hinter = Some(
        new Hinter {
          override def color = fansi.Color.LightGray

          override def hint(line: String): Option[String] = {
            keywords.filter(_.startsWith(line)).maxByOption(_.length)
          }
        }
      ),
    )

    for (line <- cli.lines("> ")) {
      println(s"Read '$line'")
    }
  }
}
