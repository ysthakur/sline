package facade

import scala.scalanative.unsafe._

@extern
object replxx {
  type ReplxxColor = CInt

  type ReplxxAction = CInt

  type ReplxxActionResult = CInt

  type ReplxxState = CStruct2[CString, Int]

  type Replxx

  type ReplxxHistoryScan

  type ReplxxHistoryEntry = CStruct2[CString, CString]

  def replxx_init(): Replxx = extern

  def replxx_end(replxx: Replxx): Unit = extern

  type replxx_modify_callback_t =
    CFuncPtr3[Ptr[CString], Ptr[Int], Ptr[Byte], Unit]

  def replxx_set_modify_callback(
      replxx: Replxx,
      callback: replxx_modify_callback_t,
      user_data: Ptr[Byte],
  ): Unit = extern

  type replxx_highlighter_callback_t =
    CFuncPtr4[CString, ReplxxColor, Int, Ptr[Byte], Unit]

  def replxx_set_highlighter_callback(
      replxx: Replxx,
      callback: replxx_highlighter_callback_t,
      user_data: Ptr[Byte],
  ): Unit = extern

  type replxx_completions

  type replxx_completion_callback_t =
    CFuncPtr4[CString, replxx_completions, Ptr[Int], Ptr[Byte], Unit]

  def replxx_set_completion_callback(
      replxx: Replxx,
      callback: replxx_modify_callback_t,
      user_data: Ptr[Byte],
  ): Unit = extern

  def replxx_add_completion(
      completions: replxx_completions,
      str: CString,
  ): Unit = extern

  def replxx_add_color_completion(
      completions: replxx_completions,
      str: CString,
      color: ReplxxColor,
  ): Unit = extern

  type replxx_hints

  type replxx_hint_callback_t =
    CFuncPtr5[CString, replxx_hints, Ptr[Int], ReplxxColor, Ptr[Byte], Unit]

  def replxx_set_hint_callback(
      replxx: Replxx,
      callback: replxx_hint_callback_t,
      user_data: Ptr[Byte],
  ): Unit = extern

  type key_press_handler_t = CFuncPtr2[Int, Ptr[Byte], ReplxxActionResult]

  def replxx_add_hint(hints: replxx_hints, str: CString): Unit = extern

  def replxx_input(replxx: Replxx, prompt: CString): CString = extern

  def replxx_get_state(replxx: Replxx, state: ReplxxState): Unit = extern

  def replxx_set_state(replxx: Replxx, state: ReplxxState): Unit = extern

  def replxx_set_ignore_case(replxx: Replxx, ignore_case: Int): Unit = extern

  def replxx_print(replxx: Replxx, fmt: CString, args: CVarArgList): Unit =
    extern

  def replxx_write(replxx: Replxx, str: CString, len: Int): Unit = extern

  def replxx_set_prompt(replxx: Replxx, prompt: CString): Unit = extern
}
