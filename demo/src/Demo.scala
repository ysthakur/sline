import sline.{Cli, Completer, Highlighter}

object Demo {
  def main(args: Array[String]): Unit = {
    val keywords = Seq("foo", "bar", "baz")
    val cli = Cli(
      completer = new Completer.Strings(keywords),
      highlighter =
        new Highlighter.Words(
          keywords,
          {
            case "foo" =>
              fansi.Color.Red
            case "bar" =>
              fansi.Color.Blue
            case "baz" =>
              fansi.Color.Green
            case _ =>
              fansi.Color.Reset
          },
        ),
    )

    for (line <- cli.lines("> ")) {
      println(s"Read '$line'")
    }
  }
}
