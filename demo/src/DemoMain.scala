import snic.{Function, History, Keymap, LineReader, Terminal}
import snic.facade.readline

import scalanative.unsafe.*

@main
def main(): Unit = {
  val keymap = Keymap.Emacs
  keymap.bindKeyseq("\\e[A", (a: CInt, b: CInt) => {println("foo!"); a})

  val terminal = Terminal(history = Some(History()), keymap = keymap)
  val reader = LineReader(terminal = terminal)

  terminal.start()

  readline.add_history(c"Foo")
  readline.add_history(c"asdf")
  println(reader.readLine("bar"))
}
