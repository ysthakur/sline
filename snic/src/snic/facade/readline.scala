package snic.facade

import scala.scalanative.unsafe._

@link("readline")
@extern
object readline {
  type rl_command_func_t = CFuncPtr2[CInt, CInt, CInt]
  type rl_vcpfunc_t = CFuncPtr1[CString, Unit]

  def readline(prompt: CString): CString = extern

  // Readline variables
  var rl_line_buffer: CString = extern

  // Selecting a keymap
  type KEYMAP_ENTRY = CStruct2[Char, rl_command_func_t]
  type Keymap = Ptr[KEYMAP_ENTRY]

  def rl_make_keymap(): Keymap = extern
  def rl_get_keymap(): Keymap = extern
  def rl_set_keymap(keymap: Keymap): Unit = extern
  def rl_get_keymap_by_name(name: CString): Keymap = extern
  def rl_get_keymap_name(keymap: Keymap): CString = extern
  def rl_set_keymap_name(name: CString, keymap: Keymap): CInt = extern

  // Binding keys
  def rl_bind_key_in_map(
      key: CInt,
      function: rl_command_func_t,
      map: Keymap
  ): CInt = extern
  def rl_bind_key_if_unbound_in_map(
      key: CInt,
      function: rl_command_func_t,
      map: Keymap
  ): CInt = extern
  def rl_unbind_key_in_map(key: CInt, map: Keymap): CInt = extern
  def rl_unbind_function_in_map(
      function: rl_command_func_t,
      map: Keymap
  ): CInt = extern
  def rl_unbind_command_in_map(command: CString, map: Keymap): CInt = extern
  def rl_bind_keyseq_in_map(
      keyseq: CString,
      function: rl_command_func_t,
      map: Keymap
  ): CInt = extern
  def rl_bind_keyseq_if_unbound_in_map(
      keyseq: CString,
      function: rl_command_func_t,
      map: Keymap
  ): CInt = extern

  // Associating function names and bindings
  def rl_named_function(name: CString): rl_command_func_t = extern
  def rl_add_funmap_entry(name: CString, function: rl_command_func_t): CInt =
    extern

  // Redisplay
  def rl_redisplay(): Unit = extern
  def rl_forced_update_display(): Unit = extern

  // Modifying text
  def rl_insert_text(next: CString): CInt = extern
  def rl_delete_text(start: CInt, end: CInt): CInt = extern

  // Character Input
  def rl_read_key(): CInt = extern

  // Terminal Management
  def rl_prep_terminal(meta_flag: CInt): Unit = extern
  def rl_deprep_terminal(): Unit = extern

  // Utility Functions
  def rl_replace_line(text: CString, clear_undo: CInt): Unit = extern
  def rl_ding(): CInt = extern

  // Alternate interface
  def rl_callback_handler_install(
      prompt: CString,
      lhandler: rl_vcpfunc_t
  ): Unit = extern
  def rl_callback_read_char(): Unit = extern

  /////////////////////////////////////////
  //////////// History ///////////////////
  //////////////////////////////////////////

  // History storage
  type histdata_t = Ptr[Byte]
  type HIST_ENTRY = CStruct3[CString, CString, histdata_t]

  // Initializing History and State Management
  def using_history(): Unit = extern

  // History List Management
  def add_history(string: CString): Unit = extern
  def remove_history(which: CInt): Ptr[HIST_ENTRY] = extern
  def free_history_entry(histent: Ptr[HIST_ENTRY]): histdata_t = extern
}
