package org.jline.terminal

import java.nio.charset.Charset

trait Terminal {
  def name: String

  // def encoding: Charset
}
