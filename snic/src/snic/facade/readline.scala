package snic.facade

import scala.scalanative.unsafe.*

@link("readline")
@extern
object readline {
  type rl_command_func_t = CFuncPtr2[CInt, CInt, CInt]

  def readline(prompt: CString): CString = extern

  // Selecting a keymap
  type KEYMAP_ENTRY = CStruct2[Char, rl_command_func_t]
  type Keymap = Ptr[KEYMAP_ENTRY]

  def rl_make_keymap(): Keymap = extern
  def rl_get_keymap(): Keymap = extern
  def rl_set_keymap(keymap: Keymap): Unit = extern
  def rl_get_keymap_by_name(name: CString): Keymap = extern
  def rl_get_keymap_name(keymap: Keymap): CString = extern
  def rl_set_keymap_name(name: CString, keymap: Keymap): CInt = extern

  // Redisplay
  def rl_redisplay(): Unit = extern

  // Character Input
  def rl_read_key(): CInt = extern

  // Terminal Management
  def rl_prep_terminal(meta_flag: CInt): Unit = extern
  def rl_deprep_terminal(): Unit = extern

  // Utility Functions
  def rl_ding(): CInt = extern

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
