import sline.{Cli, Completer, Highlighter}

object Demo {
  def main(args: Array[String]): Unit = {
    val keywords = Seq("foo", "bar", "baz")
    val cli = Cli(
      completer = new Completer.Strings(keywords),
      highlighter = new Highlighter.Strings(keywords),
    )

    for (line <- cli.lines("> ")) {
      println(s"Read '$line'")
    }
  }
}
