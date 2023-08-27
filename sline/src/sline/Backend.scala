package sline

trait Backend {
  /** Set the prompt asynchronously while [[readLine]] is still executing */
  def setPrompt(prompt: String): Unit

  def readLine(prompt: String): String

  def close(): Unit
}
