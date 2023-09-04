package sline

import java.util.regex.Pattern

import org.jline.reader.{
  Candidate,
  EndOfFileException,
  LineReader,
  LineReaderBuilder,
  ParsedLine,
  UserInterruptException,
}
import org.jline.reader.impl.BufferImpl
import org.jline.utils.AttributedString
import org.jline.widget.AutosuggestionWidgets
import org.jline.widget.Widgets

/** JVM implementation of the CLI using JLine */
class JLineCli(val reader: LineReader) extends Cli {

  override def readLine(prompt: String): Option[String] =
    try {
      Some(reader.readLine(prompt))
    } catch {
      case _: EndOfFileException =>
        None
      case _: UserInterruptException =>
        None
    }
}

object JLineCli {
  def apply(
      completer: Option[Completer],
      highlighter: Option[Highlighter],
      hinter: Option[Hinter],
  ): JLineCli = {
    val builder = LineReaderBuilder.builder()
    completer.foreach { completer =>
      builder.completer(new JLineCli.CompleterDelegate(completer))
    }
    highlighter.foreach { highlighter =>
      builder.highlighter(new JLineCli.HighlighterDelegate(highlighter))
    }
    val reader = builder.build()
    hinter.foreach { hinter =>
      new JLineCli.HinterWidgets(hinter, reader).enable()
    }

    new JLineCli(reader)
  }

  class CompleterDelegate(completerImpl: Completer)
      extends org.jline.reader.Completer {
    override def complete(
        reader: LineReader,
        line: ParsedLine,
        candidates: java.util.List[Candidate],
    ): Unit =
      completerImpl
        .complete(line.line())
        .foreach { completion =>
          candidates.add(new Candidate(completion))
        }
  }

  class HighlighterDelegate(highlighterImpl: Highlighter)
      extends org.jline.reader.Highlighter {
    override def highlight(
        reader: LineReader,
        buffer: String,
    ): AttributedString = {
      AttributedString.fromAnsi(highlighterImpl.highlight(buffer).render)
    }

    override def setErrorIndex(errorIndex: Int): Unit = ???

    override def setErrorPattern(errorPattern: Pattern): Unit = ???
  }

  /** Large chunks of this were shamelessly copied from JLine's
    * AutosuggestionWidgets.java and TailTipWidgets.java
    */
  class HinterWidgets(hinter: Hinter, reader: LineReader)
      extends Widgets(reader) {
    import HinterWidgets.{
      EndOfLineWidget,
      ForwardCharWidget,
      ForwardWordWidget,
      RedisplayWidget,
      SelfInsertWidget,
    }

    private var enabled = false

    this.addWidget(
      Widgets.TT_ACCEPT_LINE,
      () => {
        this.clearTailTip()
        this.callWidget(LineReader.ACCEPT_LINE)
        true
      },
    )
    this
      .addWidget(ForwardCharWidget, () => this.accept(LineReader.FORWARD_CHAR))
    this.addWidget(EndOfLineWidget, () => this.accept(LineReader.END_OF_LINE))
    this.addWidget(ForwardWordWidget, () => this.autosuggestForwardWord())
    this.addWidget(RedisplayWidget, () => this.doTailtip(LineReader.REDISPLAY))
    this
      .addWidget(SelfInsertWidget, () => this.doTailtip(LineReader.SELF_INSERT))
    this.addWidget(
      Widgets.TAILTIP_TOGGLE,
      () => {
        if (this.enabled)
          this.disable()
        else
          this.enable()
        this.enabled
      },
    )

    def enable(): Unit = {
      if (!this.enabled) {
        this.aliasWidget(Widgets.TT_ACCEPT_LINE, LineReader.ACCEPT_LINE)
        this.aliasWidget(ForwardCharWidget, LineReader.FORWARD_CHAR)
        this.aliasWidget(EndOfLineWidget, LineReader.END_OF_LINE)
        this.aliasWidget(ForwardWordWidget, LineReader.FORWARD_WORD)
        this.aliasWidget(RedisplayWidget, LineReader.REDISPLAY)
        this.aliasWidget(SelfInsertWidget, LineReader.REDISPLAY)

        this.setSuggestionType(LineReader.SuggestionType.TAIL_TIP)
        this.enabled = true

        try {
          this.callWidget(LineReader.REDRAW_LINE)
        } catch {
          case _: Exception => // ignore
        }
      }
    }

    def disable(): Unit = {
      if (this.enabled) {
        this.aliasWidget("." + LineReader.ACCEPT_LINE, LineReader.ACCEPT_LINE)
        this.aliasWidget("." + LineReader.FORWARD_CHAR, LineReader.FORWARD_CHAR)
        this.aliasWidget("." + LineReader.END_OF_LINE, LineReader.END_OF_LINE)
        this.aliasWidget("." + LineReader.FORWARD_WORD, LineReader.FORWARD_WORD)
        this.aliasWidget("." + LineReader.REDISPLAY, LineReader.REDISPLAY)
        this.aliasWidget("." + LineReader.SELF_INSERT, LineReader.SELF_INSERT)

        this.setSuggestionType(LineReader.SuggestionType.NONE)
        this.enabled = false

        try {
          this.callWidget(LineReader.REDRAW_LINE)
        } catch {
          case _: Exception => // ignore
        }
      }
    }

    override def tailTip(): String = {
      val hint = this.getHint().getOrElse("")
      println(hint)
      hint
    }

    private def getHint(): Option[String] =
      hinter.hint(this.buffer().toString())

    private def doTailtip(widget: String): Boolean = {
      this.callWidget(widget)
      this.getHint() match {
        case Some(hint) =>
          this.setTailTip(hint)
          this.addDescription(java.util.List.of(new AttributedString(hint)))
        case None =>
          this.clearTailTip()
      }
      true
    }

    private def accept(widget: String): Boolean = {
      val buffer = this.buffer()
      if (buffer.cursor() == buffer.length()) {
        this.getHint().foreach(this.putString)
      } else {
        this.callWidget(widget)
      }
      true
    }

    private def autosuggestForwardWord(): Boolean = {
      val buffer = this.buffer()
      if (buffer.cursor() == buffer.length()) {
        val curPos = buffer.cursor()
        buffer.write(this.tailTip())
        buffer.cursor(curPos)
        this.replaceBuffer(buffer)
        this.callWidget(LineReader.FORWARD_WORD)
        val newBuf = new BufferImpl()
        newBuf.write(buffer.substring(0, buffer.cursor()))
        this.replaceBuffer(newBuf)
      } else {
        callWidget(LineReader.FORWARD_WORD)
      }
      true
    }
  }

  object HinterWidgets {
    private val ForwardCharWidget = "_hinter-forward-char"
    private val EndOfLineWidget = "_hinter-end-of-line"
    private val ForwardWordWidget = "_hinter-forward-word"
    private val RedisplayWidget = "_hinter-redisplay"
    private val SelfInsertWidget = "_hinter-self-insert"
  }
}
