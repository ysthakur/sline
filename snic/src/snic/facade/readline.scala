package snic.facade

import scala.scalanative.unsafe.*

// @link("readline")
// @extern
object readline {
  def readline(prompt: CString): CString = extern
}
