package sline.jline

import sline.Cli

import org.jline.reader.{EndOfFileException, LineReader, UserInterruptException}

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
