package snic

import snic.facade.readline

case class Terminal(history: Option[History] = None) {
  def start(): Unit = {
    readline.rl_prep_terminal(0)

    history.foreach(_.startUsing())
  }
}
