package org.jline.terminal

import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicReference

class TerminalBuilder private () {
  private var name: String | Null = null
  private var in: InputStream | Null = null
  private var out: OutputStream | Null = null
  private var encoding: Charset | Null = null
  private var system: Boolean | Null = null
  private var dumb: Boolean | Null = null
  private var color: Boolean | Null = null

  def name(name: String): TerminalBuilder = {
    this.name = name
    this
  }

  def streams(in: InputStream, out: OutputStream): TerminalBuilder = {
    this.in = in
    this.out = out
    this
  }

  def system(system: Boolean): TerminalBuilder = {
    this.system = system
    this
  }

  def dumb(dumb: Boolean): TerminalBuilder = {
    this.dumb = dumb
    this
  }

  def color(color: Boolean): TerminalBuilder = {
    this.color = color
    this
  }

  def build(): Terminal = {
    val termOverride = TerminalBuilder.TERMINAL_OVERRIDE.get()
    if (termOverride != null) {
      return termOverride
    }

    val name =
      if (this.name == null)
        "Jline terminal"
      else
        this.name

    val encoding =
      if (this.encoding != null)
        this.encoding
      else {
        val charsetName = System.getProperty(TerminalBuilder.PROP_ENCODING)
        if (charsetName != null && Charset.isSupported(charsetName))
          Charset.forName(charsetName)
        else
          null
      }

    val dumb =
      if (this.dumb == null)
        java
          .lang
          .Boolean
          .parseBoolean(System.getProperty(TerminalBuilder.PROP_DUMB))
      else
        this.dumb

    if (dumb == true)
      DumbTerminal(name, this.in)
    else
      ReplxxTerminal(name)
  }
}

object TerminalBuilder {
  final val PROP_ENCODING = "org.jline.terminal.encoding"
  final val PROP_TYPE = "org.jline.terminal.type"
  final val PROP_DUMB = "org.jline.terminal.dumb"
  final val PROP_DUMB_COLOR = "org.jline.terminal.dumb.color"
  final val PROP_OUTPUT = "org.jline.terminal.output"
  final val PROP_OUTPUT_OUT = "out"
  final val PROP_OUTPUT_ERR = "err"
  final val PROP_OUTPUT_OUT_ERR = "out-err"
  final val PROP_OUTPUT_ERR_OUT = "err-out"

  final private val SYSTEM_TERMINAL = new AtomicReference[Terminal]()
  final private val TERMINAL_OVERRIDE = new AtomicReference[Terminal]()
}
