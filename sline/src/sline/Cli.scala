package sline

/** An interactive CLI/REPL/shell */
trait Cli {
  /** Read a line of input using the given prompt
    *
    * @returns
    *   Some(line) if a line was read, or None if either the input ended or the
    *   user pressed Ctrl+C
    */
  def readLine(prompt: String): Option[String]

  /** An iterator over the lines read, for convenience.
    *
    * If you use this method, note that each line will use the same prompt.
    *
    * @param prompt
    *   The prompt to use
    */
  def lines(prompt: String): Iterator[String] =
    Iterator
      .continually {
        readLine(prompt)
      }
      .takeWhile(_.nonEmpty)
      .map(_.get)
}
