import sline.{Cli, Completer}

@main
def main(): Unit = {
  val cli = Cli()
  while {
    val line = cli.readLine("> ")
    if (line != null) {
      println(line)
      true
    } else {
      false
    }
  } do {}
}
