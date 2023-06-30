package snic

import snic.facade.readline
import snic.highlight.{DummyHighlighter, Highlighter}

import scala.scalanative.libc.stdlib
import scala.scalanative.unsafe._

object Terminal {
  // todo this is awful
  var history: History = null
  var highlighter: Highlighter = null
  var completer: Completer = null
  var keymap: Keymap = Keymap.Emacs

  private var hitLineEnd = false
  private var hitEOF = false

  def start(): Unit = Zone { implicit z =>
    // readline.rl_prep_terminal(0)
    readline.rl_set_keymap(keymap.internal)
    if (history != null)
      history.startUsing()
    if (completer != null)
      completer.register()
    readline.rl_callback_handler_install(c"... ", lineHandler(_))
  }

  /** What is currently in readline's line buffer */
  def buffer: String = fromCString(readline.rl_line_buffer)

  def readLine(prompt: String): String = Zone { implicit z =>
    while (!hitLineEnd && !hitEOF) {
      readline.rl_callback_read_char()
      readline.rl_replace_line(toCString(highlighter.highlight(buffer)), 0)
      readline.rl_redisplay()
    }
    // val cLine = readline.readline(toCString(prompt))
    // val line = fromCString(cLine)
    // stdlib.free(cLine)
    // // println(s"Line buffer: ${fromCString(readline.rl_line_buffer)}")
    // for (hl <- highlighter) {
    //   readline.rl_replace_line(toCString(hl.highlight(line)), 0)
    //   readline.rl_redisplay()
    //   // readline.rl_forced_update_display()
    //   // println(s"Line buffer after: ${fromCString(readline.rl_line_buffer)}")
    // }
    // println(highlighter.fold(line)(_.highlight(line)))
    // history.foreach(_.addHistory(line))
    // line
    buffer
  }

  private def lineHandler(cLine: CString): Unit = Zone { implicit z =>
    hitLineEnd = true
    if (cLine == null) {
      hitEOF = true
      return
    }
    val line = fromCString(cLine)
    stdlib.free(cLine)
    println(s"Handling line $line")
    // if (highlighter != null) {
    //   println("here1")
    //   // readline.rl_replace_line(toCString(highlighter.highlight(line)), 0)
    //   // readline.rl_insert_text(toCString(highlighter.highlight(line)))
    //   // println("here2")
    //   // readline.rl_redisplay()
    // }
    if (history != null)
      history.addHistory(line)
  }
}
