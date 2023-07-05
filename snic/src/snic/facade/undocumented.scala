package snic.facade

import scala.scalanative.unsafe._

@extern
object undocumented {
  def insert_some_chars(string: CString, count: CInt, col: CInt): Unit = extern
}
