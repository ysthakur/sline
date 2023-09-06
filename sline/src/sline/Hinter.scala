package sline

/** For providing inline hints */
trait Hinter {
  /** The color of the hints */
  def color: fansi.Attr

  /** A hint for the current line, if possible */
  def hint(line: String): Option[String]
}
