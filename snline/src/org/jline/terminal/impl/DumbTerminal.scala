package org.jline.terminal

import java.io.InputStream

class DumbTerminal(val name: String, val in: InputStream) extends Terminal
