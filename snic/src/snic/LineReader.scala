package snic

import snic.facade.readline
import snic.highlight.DummyHighlighter
import snic.highlight.Highlighter

import scalanative.unsafe.*

case class LineReader(
    terminal: Terminal,
    highlighter: Highlighter = DummyHighlighter
) {
  def readLine(prompt: String): String = Zone { implicit z =>
    val line = fromCString(readline.readline(toCString(prompt)))
    println(highlighter.highlight(line))
    line
  }
}
