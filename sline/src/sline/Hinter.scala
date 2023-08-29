package sline

trait Hinter {
  def color: fansi.Attr

  def hint(line: String): Option[String]
}
