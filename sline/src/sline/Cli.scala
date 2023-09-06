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

object Cli {
  /** Create a CLI with the default backend for the current platform
    *
    * The reason for having a separate [[CliImpl]] object that's duplicated for
    * each platform is that this [[apply]] method has parameters with default
    * values, and if we were to duplicate [[Cli]] for every platform, each of
    * those would need to add the same default values to its parameters, which
    * would be a bit tougher to maintain.
    */
  def apply(
      completer: Completer = null,
      highlighter: Highlighter = null,
      hinter: Hinter = null,
  ): Cli = CliImpl(Option(completer), Option(highlighter), Option(hinter))
}
