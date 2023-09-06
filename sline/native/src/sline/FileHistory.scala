package sline

import scala.scalanative.unsafe.*

import sline.replxx.*

// TODO possible segfaults here because of toCString
/** Built-in replxx file-based history */
class FileHistory(repl: Replxx, filename: String) extends History {
  Zone { implicit z =>
    replxx_history_load(repl, toCString(filename))
  }

  override def add(line: String): Unit =
    Zone { implicit z =>
      replxx_history_add(repl, toCString(line))
    }

  def save(): Unit =
    Zone { implicit z =>
      replxx_history_save(repl, toCString(filename))
    }
}
