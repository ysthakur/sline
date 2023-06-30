package snic

import snic.facade.readline

import scalanative.unsafe._

class History {
  private[snic] def startUsing(): Unit = {
    // readline.using_history()
  }

  def addHistory(line: String): Unit = Zone { implicit z =>
    if (line.nonEmpty) {
      readline.add_history(toCString(line))
    }
  }
}
