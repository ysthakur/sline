package sline.jline

import java.nio.file.Path
import java.time.Instant
import java.util.ListIterator

import org.jline.reader.{History, LineReader}

class HistoryDelegate(hist: sline.History) extends org.jline.reader.History {
  override def attach(reader: LineReader): Unit = ???

  override def load(): Unit = ???

  override def save(): Unit = ???

  override def write(file: Path, incremental: Boolean): Unit = ???

  override def append(file: Path, incremental: Boolean): Unit = ???

  override def read(file: Path, checkDuplicates: Boolean): Unit =
    hist.load(file)

  override def purge(): Unit = ???

  override def size(): Int = ???

  override def index(): Int = ???

  override def first(): Int = ???

  override def last(): Int = ???

  override def get(index: Int): String = ???

  override def add(time: Instant, line: String): Unit = hist.add(line)

  override def iterator(index: Int): ListIterator[History.Entry] = ???

  override def current(): String = ???

  override def previous(): Boolean = ???

  override def next(): Boolean = ???

  override def moveToFirst(): Boolean = ???

  override def moveToLast(): Boolean = ???

  override def moveTo(index: Int): Boolean = ???

  override def moveToEnd(): Unit = ???

  override def resetIndex(): Unit = ???
}
