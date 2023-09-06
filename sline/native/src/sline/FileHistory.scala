package sline

import scala.scalanative.unsafe.*

import sline.replxx.*

class FileHistory(filename: String) extends History {
  private var repl: Replxx = null
  private var replxxHistory: ReplxxHistory = null

  private[sline] def setRepl(repl: Replxx): Unit =
    Zone { implicit z =>
      this.repl = repl
      replxx_history_load(repl, toCString(filename))
    }

  override def add(line: String): Unit =
    Zone { implicit z =>
      // TODO possible segfault because of toCString
      replxx_history_add(repl, toCString(line))
    }

  def save(): Unit =
    Zone { implicit z =>
      replxx_history_save(repl, toCString(filename))
    }
}
