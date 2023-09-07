package sline

import java.nio.file.Path

/** This helps ensure the same API for both JVM and Native, as well as provides
  * the same doc comments for both CliBuilders.
  */
trait AbstractCliBuilder {
  def completer(completer: Completer): this.type

  def highlighter(highlighter: Highlighter): this.type

  def hinter(hinter: Hinter): this.type

  def history(history: History): this.type

  /** Use the default history implementation for the CLI provided by either
    * Replxx or JLine
    */
  def defaultHistory(): this.type

  def build(): Cli
}
