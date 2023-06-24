package snic

import snic.facade.readline
import scala.scalanative.unsafe.*

case class Terminal(history: Option[History] = None, keymap: Keymap = Keymap.Emacs) {
  def start(): Unit = Zone { implicit z =>
    readline.rl_prep_terminal(0)
    readline.rl_set_keymap(keymap.internal)
    history.foreach(_.startUsing())
  }
}
