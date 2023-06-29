import snic.{Function, History, Keymap, LineReader, Terminal}
import snic.facade.readline
import snic.highlight.FansiRegexHighlighter

import scalanative.unsafe.*

@main
def main(): Unit = {
  val keymap = Keymap.Emacs
  keymap.bindKeyseq(
    "\\e[A",
    (a: CInt, b: CInt) => {
      println("foo!");
      a
    }
  )

  val highlighter = FansiRegexHighlighter(
    List(
      raw"\w+".r -> fansi.Attrs(fansi.Color.Red),
      raw"\d+".r -> fansi.Attrs(fansi.Color.Blue, fansi.Bold.On)
    )
  )

  val terminal = Terminal(history = Some(History()), keymap = keymap)
  val reader = LineReader(terminal = terminal, highlighter = highlighter)

  terminal.start()

  readline.add_history(c"Foo")
  readline.add_history(c"asdf")
  reader.readLine("bar")
}
