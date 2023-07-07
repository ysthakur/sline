package snic

import snic.facade.readline

import scalanative.unsafe._

object Function {
  def get(name: String): readline.rl_command_func_t = Zone { implicit z =>
    readline.rl_named_function(toCString(name))
  }

  /** Add `name` to the list of named functions. Set key to something other than
    * -1 to also bind it to the given function.
    */
  def named(name: String, key: Int = -1)(
      f: readline.rl_command_func_t
  ): readline.rl_command_func_t = Zone { implicit z =>
    readline.rl_add_defun(toCString(name), f, key)
    f
  }

  def undoGroup(f: => Unit): Unit = {
    readline.rl_begin_undo_group()
    f
    readline.rl_end_undo_group()
  }
}
