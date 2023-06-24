package snic.facade

import scala.scalanative.unsafe.*

@link("readline")
@extern
object readline {
  var rl_line_buffer: CString = extern
  var rl_point: CInt = extern
  var rl_end: CInt = extern
  var rl_mark: CInt = extern
  var rl_done: CInt = extern
  var rl_eof_found: CInt = extern
  var rl_num_chars_to_read: CInt = extern
  var rl_pending_input: CInt = extern
  var rl_dispatching: CInt = extern
  var rl_erase_empty_line: CInt = extern
  val rl_prompt: CString = extern
  var rl_display_prompt: CString = extern
  var rl_already_prompted: CInt = extern
  val rl_library_version: CString = extern
  val rl_readline_version: CInt = extern
  val rl_terminal_name: CString = extern
  val rl_readline_name: CString = extern
  // todo do rl_instream and everything after that

  def readline(prompt: CString): CString = extern
}
