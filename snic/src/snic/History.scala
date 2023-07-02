package snic

import snic.facade.readline

import scalanative.unsafe._

object History {
  private[snic] def startUsing(): Unit = readline.using_history()

  def addLine(line: String): Unit = Zone { implicit z =>
    if (line.nonEmpty) { readline.add_history(toCString(line)) }
  }

  def clear(): Unit = readline.clear_history()

  def historyList(): List[HistoryEntry] = {
    var list = readline.history_list()
    val res = ListBuffer.empty[HistoryEntry]
    while (list(0) != null) {
      res.append(readHistoryEntry(list(0)))
      list += sizeof[Ptr[readline.HIST_ENTRY]]
    }
    res.toList
  }

  private def readHistoryEntry(entry: Ptr[readline.HIST_ENTRY]): HistoryEntry =
    HistoryEntry(fromCString(entry._1), fromCString(entry._2))

  case class HistoryEntry(line: String, timestamp: String)
}
