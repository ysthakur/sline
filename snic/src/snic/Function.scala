package snic

import snic.facade.readline

import scalanative.unsafe.*

object Function {
  def get(name: String): readline.rl_command_func_t = Zone { implicit z =>
    readline.rl_named_function(toCString(name))
  }
}
