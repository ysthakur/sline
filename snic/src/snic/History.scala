package snic

import snic.facade.readline

case class History() {
  private[snic] def startUsing(): Unit = {
    readline.using_history()
  }
}
