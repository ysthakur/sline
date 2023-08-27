import scala.io.AnsiColor
import scalanative.unsafe.*

import sline.ReplxxBackend
import sline.Sline

@main
def main(): Unit = {
  val backend = ReplxxBackend(null)
  val cli = Sline(null, null, null, backend = backend)
  while (true) {
    println(cli.readLine(">"))
  }
}
