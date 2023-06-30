import snic.{Function, History, Keymap, Terminal}
import snic.facade.readline
import snic.highlight.FansiRegexHighlighter

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

  Terminal.history = History()

  Terminal.highlighter = FansiRegexHighlighter(
    List(
      raw"\w+".r -> fansi.Attrs(fansi.Color.Red),
      raw"\d+".r -> fansi.Attrs(fansi.Color.Blue, fansi.Bold.On),
    )
  )

  Terminal.start()

  readline.add_history(c"Foo")
  readline.add_history(c"asdf")
  Terminal.setPrompt(fansi.Color.Green(fansi.Underlined.On("> ")).render)
  Terminal.readLine()
  Terminal.setPrompt(fansi.Bold.On(fansi.Color.Red(">> ")).render)
  Terminal.readLine()
  Terminal.readLine()
  Terminal.readLine()
}
