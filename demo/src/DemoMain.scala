import snic.{History, Keymap, LineReader, Terminal}
import snic.facade.readline

import scalanative.unsafe.*

@main
def main(): Unit = {
  val terminal = Terminal(Some(History()), keymap = Keymap.Vi)
  val reader = LineReader(terminal = terminal)

  terminal.start()

  readline.add_history(c"Foo")
  readline.add_history(c"asdf")
  println(reader.readLine("bar"))
}
