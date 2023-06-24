package snic

import snic.facade.readline

import scala.scalanative.unsafe.*

class Keymap private (private[snic] val internal: readline.Keymap) {
  /** Bind a key to a function
    * @param overwrite
    *   Whether to overwrite any previous bindings for the key
    * @see
    *   [unbindKey]
    * @see
    *   [bindKeyseq]
    */
  def bindKey(
      key: Int,
      fn: readline.rl_command_func_t,
      overwrite: Boolean = true
  ): Unit = {
    val res =
      if (overwrite) {
        readline.rl_bind_key_in_map(key, fn, internal)
      } else {
        readline.rl_bind_key_if_unbound_in_map(key, fn, internal)
      }
    if (res != 0) {
      throw new IllegalArgumentException(s"Invalid key $key")
    }
  }

  /** Bind a key sequence to a function
    * @param overwrite
    *   Whether to overwrite any previous bindings for the key sequence
    * @see
    *   [bindKey]
    */
  def bindKeyseq(
      keyseq: String,
      fn: readline.rl_command_func_t,
      overwrite: Boolean = true
  ): Unit = Zone { implicit z =>
    val cseq = toCString(keyseq)
    val res =
      if (overwrite) {
        readline.rl_bind_keyseq_in_map(cseq, fn, internal)
      } else {
        readline.rl_bind_keyseq_if_unbound_in_map(cseq, fn, internal)
      }
    if (res != 0) {
      throw new IllegalArgumentException(s"Invalid keyseq $keyseq")
    }
  }

  def unbindKey(key: Int): Int = readline.rl_unbind_key_in_map(key, internal)
}

object Keymap {
  val Emacs = Keymap.get("emacs")
  val Vi = Keymap.get("vi")

  /** Make a new keymap without a name */
  def apply(): Keymap = Zone { implicit z =>
    new Keymap(readline.rl_make_keymap())
  }

  /** Make a new keymap and give it a name that it can be accessed with */
  def apply(name: String): Keymap = Zone { implicit z =>
    val keymap = readline.rl_make_keymap()
    readline.rl_set_keymap_name(toCString(name), keymap)
    new Keymap(keymap)
  }

  /** Get a named keymap (either a built-in one or a previously stored one) */
  def get(name: String) = Zone { implicit z =>
    new Keymap(readline.rl_get_keymap_by_name(toCString(name)))
  }
}
