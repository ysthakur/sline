import sline.{CliBuilder, Completer, Highlighter, Hinter}

object Demo {
  def main(args: Array[String]): Unit = {
    val keywords = Seq("foobar", "bar", "baz")
    val cli = new CliBuilder()
      .completer(new Completer.Strings(keywords))
      .highlighter(
        new Highlighter.Words(keyword =>
          if (keywords.contains(keyword))
            fansi.Color.Blue
          else
            fansi.Color.Reset
        )
      )
      .hinter(
        new Hinter {
          override def color = fansi.Color.LightGray

          override def hint(line: String): Option[String] = {
            Option
              .when(line.nonEmpty) {
                keywords
                  .filter(_.startsWith(line))
                  .maxByOption(_.length)
                  .map(_.substring(line.length))
              }
              .flatten
          }
        }
      )
      .build()

    for (line <- cli.lines("> ")) {
      println(s"Read '$line'")
    }
  }
}
