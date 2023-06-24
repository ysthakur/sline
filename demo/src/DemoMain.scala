import snic.{History, LineReader, Terminal}
import snic.facade.readline

import scalanative.unsafe.*

@main
def main(): Unit = {
  val terminal = Terminal(Some(History()))
  val reader = LineReader(terminal)

  terminal.start()

  readline.add_history(c"Foo")
  readline.add_history(c"asdf")
  println(reader.readLine("bar"))
}
