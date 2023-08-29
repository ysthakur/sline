package sline

/** For providing inline hints */
trait Hinter {
  /** The color of the hints */
  def color: fansi.Attr

  /** A hint for the current line, if possible. The returned string should start
    * with the current line
    *
    * TODO this may cause bugs, change to only require returning the hint part?
    */
  def hint(line: String): Option[String]
}
