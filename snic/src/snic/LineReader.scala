package snic

import snic.facade.readline

import scalanative.unsafe.*

case class LineReader(term: Terminal, highlighter: Option[Highlighter] = None) {
  def readLine(prompt: String): String = Zone { implicit z =>
    fromCString(readline.readline(toCString(prompt)))
  }
}
