import scala.io.AnsiColor
import scalanative.unsafe.*

import sline.{Function, History, Keymap, RegexHighlighter, Terminal}
import sline.facade.readline

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
  Terminal.setKeymap(keymap)

  Terminal.setHistory(History())

  Terminal.setHighlighter(
    RegexHighlighter(
      List(
        raw"\w+".r -> AnsiColor.RED,
        raw"\d+".r -> (AnsiColor.BLUE + AnsiColor.BOLD),
        raw"#.*".r -> (AnsiColor.GREEN + AnsiColor.UNDERLINED),
      )
    )
  )

  Terminal.start()

  readline.add_history(c"Foo")
  readline.add_history(c"asdf")
  while (true)
    Terminal.readLine()
}
