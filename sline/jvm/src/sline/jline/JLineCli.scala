package sline.jline

import java.nio.file.Path
import java.time.Instant
import java.util.regex.Pattern
import java.util.ListIterator

import sline.Cli
import sline.Completer
import sline.Highlighter
import sline.Hinter

import org.jline.reader.{
  EndOfFileException,
  LineReader,
  LineReaderBuilder,
  UserInterruptException,
}

/** JVM implementation of the CLI using JLine */
class JLineCli(val reader: LineReader) extends Cli {
  override def readLine(prompt: String): Option[String] =
    try {
      Some(reader.readLine(prompt))
    } catch {
      case _: EndOfFileException =>
        None
      case _: UserInterruptException =>
        None
    }
}
