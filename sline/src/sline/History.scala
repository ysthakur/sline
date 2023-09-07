package sline

import java.nio.file.Path

trait History {
  def add(line: String): Unit

  def load(file: Path): Unit

  def save(file: Path): Unit
}
