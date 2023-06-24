package snic

import snic.facade.readline

import scala.scalanative.unsafe.*

class Keymap private (private[snic] val internal: readline.Keymap) {}

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
