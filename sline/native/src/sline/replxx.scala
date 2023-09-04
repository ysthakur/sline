package sline

import scala.scalanative.unsafe._

/** Facade for calling replxx stuff */
@extern
object replxx {
  type ReplxxColor = CInt

  type ReplxxAction = CInt

  type ReplxxActionResult = CInt

  type ReplxxState = CStruct2[CString, CInt]

  type Replxx

  type ReplxxHistoryScan

  type ReplxxHistoryEntry = CStruct2[CString, CString]

  def replxx_init(): Replxx = extern

  def replxx_end(replxx: Replxx): Unit = extern

  type replxx_modify_callback_t = CFuncPtr3[Ptr[CString], Ptr[CInt], Ptr[
    Byte
  ], Unit]

  def replxx_set_modify_callback(
      replxx: Replxx,
      callback: replxx_modify_callback_t,
      user_data: Ptr[Byte],
  ): Unit = extern

  type replxx_highlighter_callback_t = CFuncPtr4[CString, Ptr[
    ReplxxColor
  ], CInt, Ptr[Byte], Unit]

  def replxx_set_highlighter_callback(
      replxx: Replxx,
      callback: replxx_highlighter_callback_t,
      user_data: Ptr[Byte],
  ): Unit = extern

  type replxx_completions = Ptr[Byte]

  type replxx_completion_callback_t = CFuncPtr4[
    CString,
    replxx_completions,
    Ptr[CInt],
    Ptr[Byte],
    Unit,
  ]

  def replxx_set_completion_callback(
      replxx: Replxx,
      callback: replxx_completion_callback_t,
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

  type replxx_hints = Ptr[Byte]

  type replxx_hint_callback_t = CFuncPtr5[CString, replxx_hints, Ptr[CInt], Ptr[
    ReplxxColor
  ], Ptr[Byte], Unit]

  def replxx_set_hint_callback(
      replxx: Replxx,
      callback: replxx_hint_callback_t,
      user_data: Ptr[Byte],
  ): Unit = extern

  type key_press_handler_t = CFuncPtr2[CInt, Ptr[Byte], ReplxxActionResult]

  def replxx_add_hint(hints: replxx_hints, str: CString): Unit = extern

  def replxx_input(replxx: Replxx, prompt: CString): CString = extern

  def replxx_get_state(replxx: Replxx, state: ReplxxState): Unit = extern

  def replxx_set_state(replxx: Replxx, state: ReplxxState): Unit = extern

  def replxx_set_ignore_case(replxx: Replxx, ignore_case: CInt): Unit = extern

  def replxx_print(replxx: Replxx, fmt: CString, args: CVarArgList): Unit =
    extern

  def replxx_write(replxx: Replxx, str: CString, len: CInt): Unit = extern

  def replxx_set_prompt(replxx: Replxx, prompt: CString): Unit = extern

  def replxx_emulate_key_press(replxx: Replxx, code: CUnsignedInt): Unit =
    extern

  def replxx_invoke(
      replxx: Replxx,
      action: ReplxxAction,
      code: CUnsignedInt,
  ): ReplxxActionResult = extern

  def replxx_bind_key(
      replxx: Replxx,
      code: CInt,
      handler: key_press_handler_t,
      userData: Ptr[Byte],
  ): Unit = extern

  def replxx_set_preload_buffer(replxx: Replxx, preloadText: CString): Unit =
    extern

  def replxx_history_add(replxx: Replxx, line: CString): Unit = extern

  def replxx_history_size(replxx: Replxx): CInt = extern

  def replxx_set_word_break_characters(
      replxx: Replxx,
      wordBreakers: CString,
  ): Unit = extern

  def replxx_set_completion_count_cutoff(replxx: Replxx, count: CInt): Unit =
    extern

  def replxx_set_max_hint_rows(replxx: Replxx, count: CInt): Unit = extern

  def replxx_set_hint_delay(replxx: Replxx, milliseconds: CInt): Unit = extern

  def replxx_set_double_tab_completion(replxx: Replxx, value: CInt): Unit =
    extern

  def replxx_set_complete_on_empty(replxx: Replxx, value: CInt): Unit = extern

  def replxx_set_beep_on_ambiguous_completion(
      replxx: Replxx,
      value: CInt,
  ): Unit = extern

  def replxx_history_sync(replxx: Replxx, filename: CString): CInt = extern

  def replxx_history_save(replxx: Replxx, filename: CString): CInt = extern

  def replxx_history_load(replxx: Replxx, filename: CString): CInt = extern

  def replxx_history_clear(replxx: Replxx): Unit = extern

  def replxx_clear_screen(replxx: Replxx): Unit = extern
}
