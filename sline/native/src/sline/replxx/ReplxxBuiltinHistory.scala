package sline.replxx

import java.nio.file.Path
import scala.scalanative.unsafe.*

import sline.replxx.replxx.*
import sline.History

// TODO possible segfaults here because of toCString
/** Built-in replxx file-based history */
class ReplxxBuiltinHistory(repl: Replxx) extends History {
  override def add(line: String): Unit =
    Zone { implicit z =>
      replxx_history_add(repl, toCString(line))
    }

  override def load(file: Path): Unit =
    Zone { implicit z =>
      replxx_history_load(repl, toCString(file.toString()))
    }

  override def save(file: Path): Unit =
    Zone { implicit z =>
      replxx_history_save(repl, toCString(file.toString()))
    }
}
