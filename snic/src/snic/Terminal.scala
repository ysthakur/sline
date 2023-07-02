package snic

import snic.facade.readline
import snic.highlight.{DummyHighlighter, Highlighter}
import snic.highlight.HighlightRange

import scala.io.AnsiColor
import scala.scalanative.libc.stdlib
import scala.scalanative.unsafe._

object Terminal {
  val DefaultPrompt = "> "

  // todo this is awful
  private var history: History = null
  private var highlighter: Highlighter = null
  private var completer: Completer = null
  private var keymap: Keymap = Keymap.Emacs

  private var prevPrompt: String = null
  private var hitLineEnd = false
  private var hitEOF = false

  def setHistory(history: History): Unit = this.history = history
  def setHighlighter(highlighter: Highlighter): Unit =
    this.highlighter = highlighter
  def setCompleter(completer: Completer): Unit = this.completer = completer
  def setKeymap(keymap: Keymap): Unit = this.keymap = keymap

  def start(): Unit = Zone { implicit z =>
    // readline.rl_prep_terminal(0)
    readline.rl_set_keymap(keymap.internal)
    if (history != null) history.startUsing()
    if (completer != null) completer.register()
  }

  def close(): Unit = readline.rl_callback_handler_remove()

  def setPrompt(prompt: String): Unit = if (prompt != prevPrompt) {
    Zone { implicit z =>
      readline.rl_callback_handler_install(toCString(prompt), lineHandler(_))
    }
    this.prevPrompt = prompt
  }

  /** What is currently in readline's line buffer */
  def buffer: String = fromCString(readline.rl_line_buffer)

  /** Read a line of input */
  def readLine(): String = Zone { implicit z =>
    if (hitEOF) { null }
    else {
      if (prevPrompt == null) setPrompt(DefaultPrompt)
      while (!hitLineEnd && !hitEOF) {
        readline.rl_callback_read_char()
        // Highlight the line
        if (highlighter != null) {
          /** This changes the characters in `line` from index `start` to `end`.
            * We do this to trick readline into redisplaying those characters in
            * the color we want. See https://stackoverflow.com/a/76592983.
            */
          def modify(line: String, start: Int, end: Int): String = line
            .zipWithIndex.map { case (c, i) =>
              if (start <= i && i < end) ((c + 1) % Char.MaxValue).toChar else c
            }.mkString
          for (
            HighlightRange(start, end, ansi) <- highlighter.highlight(buffer)
          ) {
            val cText = readline.rl_copy_text(0, readline.rl_end)
            val text = fromCString(cText)
            val trickLine = toCString(modify(text, start, end))
            val trickRest = toCString(modify(text, end, readline.rl_end))

            // Color the desired range
            readline.rl_replace_line(trickLine, 0)
            readline.rl_redisplay()
            print(ansi)
            System.out.flush()

            // Reset everything after the highlighted part
            readline.rl_replace_line(trickRest, 0)
            readline.rl_redisplay()
            print(AnsiColor.RESET)
            System.out.flush()

            readline.rl_replace_line(cText, 0)
            readline.rl_redisplay()
            readline.rl_free(cText)
          }

          readline.rl_redisplay()
        }
      }
      this.hitLineEnd = false
      buffer
    }
  }

  private def lineHandler(cLine: CString): Unit = Zone { implicit z =>
    this.hitLineEnd = true
    if (cLine == null) {
      this.hitEOF = true
      this.close()
    } else {
      val line = fromCString(cLine)
      stdlib.free(cLine)
      if (history != null) history.addHistory(line)
      readline.rl_on_new_line()
    }
  }
}
