package sline

import java.nio.file.Path
import scala.scalanative.unsafe.*

import sline.replxx.*

// TODO possible segfaults here because of toCString
/** Built-in replxx file-based history */
class ReplxxFileHistory(repl: Replxx, file: Option[Path]) extends History {
  file.foreach { filePath =>
    Zone { implicit z =>
      replxx_history_load(repl, toCString(filePath.toString()))
    }
  }

  override def add(line: String): Unit =
    Zone { implicit z =>
      replxx_history_add(repl, toCString(line))
    }

  def save(file: Path): Unit =
    Zone { implicit z =>
      replxx_history_save(repl, toCString(file.toString()))
    }
}
