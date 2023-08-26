package sline

trait Backend {
  def setPrompt(prompt: String): Unit
}
