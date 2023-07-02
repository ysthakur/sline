import snic.{Function, History, Keymap, Terminal}
import snic.facade.readline
import snic.highlight.RegexHighlighter

import scala.io.AnsiColor
import scalanative.unsafe.*

@main
def main(): Unit = {
  val keymap = Keymap.Emacs
  keymap.bindKeyseq(
    "\\e[B",
    (a: CInt, b: CInt) => {
      println("foo!")
      System.out.flush()
      0
    },
  )

  Terminal.setHistory(History())

  Terminal.setHighlighter(RegexHighlighter(List(
    raw"\w+".r -> AnsiColor.RED,
    raw"\d+".r -> (AnsiColor.BLUE + AnsiColor.BOLD),
    raw"#.*".r -> AnsiColor.GREEN,
  )))

  Terminal.start()

  readline.add_history(c"Foo")
  readline.add_history(c"asdf")
  while (true) Terminal.readLine()
}
