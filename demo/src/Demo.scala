import sline.{Cli, Completer}

object Demo {
  def main(args: Array[String]): Unit = {
    val cli = Cli()

    for (line <- cli.lines("> ")) {
      println(s"Read '$line'")
    }
  }
}
