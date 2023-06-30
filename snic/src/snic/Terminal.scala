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

  private var prevPrompt: String = null
  private var hitLineEnd = false
  private var hitEOF = false

  val DefaultPrompt = "> "

  def start(): Unit = Zone { implicit z =>
    // readline.rl_prep_terminal(0)
    readline.rl_set_keymap(keymap.internal)
    if (history != null)
      history.startUsing()
    if (completer != null)
      completer.register()
  }

  def setPrompt(prompt: String): Unit =
    if (prompt != prevPrompt) {
      Zone { implicit z =>
        readline.rl_callback_handler_install(toCString(prompt), lineHandler(_))
      }
      this.prevPrompt = prompt
    }

  /** What is currently in readline's line buffer */
  def buffer: String = fromCString(readline.rl_line_buffer)

  def readLine(): String = Zone { implicit z =>
    if (hitEOF) {
      null
    } else {
      if (prevPrompt == null)
        setPrompt(DefaultPrompt)
      while (!hitLineEnd && !hitEOF) {
        readline.rl_callback_read_char()
        val highlighted = highlighter.highlight(buffer)
        readline.rl_replace_line(toCString(highlighted), 0)
        // todo restore original position instead of moving to end
        readline.rl_point = readline.rl_end
        readline.rl_redisplay()
      }
      this.hitLineEnd = false
      buffer
    }
  }

  private def lineHandler(cLine: CString): Unit = Zone { implicit z =>
    this.hitLineEnd = true
    if (cLine == null) {
      this.hitEOF = true
    } else {
      val line = fromCString(cLine)
      stdlib.free(cLine)
      println(s"Adding '$line' to history")
      if (history != null)
        history.addHistory(line)
      readline.rl_on_new_line()
    }
  }
}
