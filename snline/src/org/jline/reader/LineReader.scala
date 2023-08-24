package org.jline.reader

import scala.collection.mutable

import org.jline.terminal.Terminal

class LineReader private (
    private val terminal: Terminal,
    private val appName: String,
    private val variables: mutable.Map[String, Any],
) {}
