package sline.jline

import sline.Hinter

import org.jline.reader.impl.BufferImpl
import org.jline.reader.LineReader
import org.jline.reader.Widget
import org.jline.utils.InfoCmp
import org.jline.widget.Widgets
/** Large chunks of this were shamelessly copied from JLine's
  * AutosuggestionWidgets.java and TailTipWidgets.java
  */
class HinterWidgets(hinter: Hinter, reader: LineReader)
    extends Widgets(reader) {
  import HinterWidgets.*

  private var enabled = false

  private val widgets = Map(
    makeWidget(Widgets.TT_ACCEPT_LINE, LineReader.ACCEPT_LINE) { () =>
      this.clearTailTip()
      this.callWidget(LineReader.ACCEPT_LINE)
      true
    },
    makeWidget(ForwardCharWidget, LineReader.FORWARD_CHAR) { () =>
      this.accept(LineReader.FORWARD_CHAR)
    },
    makeWidget(ForwardWordWidget, LineReader.FORWARD_WORD) { () =>
      this.autosuggestForwardWord()
    },
    makeWidget(EndOfLineWidget, LineReader.END_OF_LINE) { () =>
      this.accept(LineReader.END_OF_LINE)
    },
    makeWidget(RedisplayWidget, LineReader.REDISPLAY) { () =>
      this.doTailtip(LineReader.REDISPLAY)
    },
    makeWidget(SelfInsertWidget, LineReader.SELF_INSERT) { () =>
      this.doTailtip(LineReader.SELF_INSERT)
    },
    makeWidget(KillLineWidget, LineReader.KILL_LINE) { () =>
      this.clearTailTip()
      this.doTailtip(LineReader.KILL_LINE)
    },
    makeWidget(KillWholeLineWidget, LineReader.KILL_WHOLE_LINE) { () =>
      this.callWidget(LineReader.KILL_WHOLE_LINE)
      this.doTailtip(LineReader.REDISPLAY)
    },
    makeWidget(DeleteCharWidget, LineReader.DELETE_CHAR) { () =>
      this.clearTailTip()
      this.doTailtip(LineReader.DELETE_CHAR)
    },
    makeWidget(BackwardDeleteCharWidget, LineReader.BACKWARD_DELETE_CHAR) {
      () =>
        this.doTailtip(LineReader.BACKWARD_DELETE_CHAR)
    },
    makeWidget(ExpandOrCompleteWidget, LineReader.EXPAND_OR_COMPLETE) { () =>
      if (this.doTailtip(LineReader.EXPAND_OR_COMPLETE)) {
        if (lastBinding().equals("\t")) {
          this.callWidget(LineReader.BACKWARD_CHAR)
          reader.runMacro(
            org
              .jline
              .keymap
              .KeyMap
              .key(reader.getTerminal(), InfoCmp.Capability.key_right)
          )
        }
        true
      } else {
        false
      }
    },
  )

  this.addWidget(
    WindowWidget,
    () => {
      callWidget(LineReader.REDRAW_LINE)
      true
    },
  )
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

  private def makeWidget(widgetName: String, jlineWidgetName: String)(
      widget: Widget
  ): (String, String) = {
    this.addWidget(widgetName, widget)
    (widgetName, jlineWidgetName)
  }

  def enable(): Unit = {
    if (!this.enabled) {
      for ((widgetName, jlineWidgetName) <- this.widgets) {
        this.aliasWidget(widgetName, jlineWidgetName)
      }

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
      for (jlineWidgetName <- this.widgets.values) {
        this.aliasWidget("." + jlineWidgetName, jlineWidgetName)
      }

      this.setSuggestionType(LineReader.SuggestionType.NONE)
      this.enabled = false

      try {
        this.callWidget(LineReader.REDRAW_LINE)
      } catch {
        case _: Exception => // ignore
      }
    }
  }

  override def tailTip(): String = this.getHint().getOrElse("")

  private def getHint(): Option[String] = hinter.hint(this.buffer().toString())

  private def doTailtip(widget: String): Boolean = {
    this.callWidget(widget)
    this.getHint() match {
      case Some(hint) =>
        this.setTailTip(hint)
      case None =>
        this.clearTailTip()
    }
    true
  }

  private def accept(widget: String): Boolean = {
    this.clearTailTip()
    val buffer = this.buffer()
    if (hinter.complete && buffer.cursor() == buffer.length()) {
      this.getHint().foreach(this.putString)
    } else {
      this.callWidget(widget)
    }
    true
  }

  private def autosuggestForwardWord(): Boolean = {
    this.clearTailTip()
    val buffer = this.buffer()
    if (hinter.complete && buffer.cursor() == buffer.length()) {
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
  private val BackwardDeleteCharWidget = "_hinter-backward-delete-char"
  private val DeleteCharWidget = "_hinter-delete-char"
  private val KillLineWidget = "_hinter-kill-line"
  private val KillWholeLineWidget = "_hinter-kill-whole-line"
  private val ExpandOrCompleteWidget = "_hinter-expand-or-complete"
  private val WindowWidget = "hinter-window"
}
