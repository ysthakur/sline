package sline

/** For providing inline hints */
trait Hinter {
  /** The color of the hints */
  def color: fansi.Attr

  /** Whether or not the hint should also be completed upon pressing the right
    * arrow
    */
  def complete: Boolean

  /** A hint for the current line, if possible */
  def hint(line: String): Option[String]
}
