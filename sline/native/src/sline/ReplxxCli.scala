package sline

import scala.collection.mutable
import scala.scalanative.libc.stdlib.{calloc, free, malloc}
import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

import sline.replxx.*

/** Scala Native implementation of the CLI using replxx */
class ReplxxCli(
    completer: Option[Completer],
    highlighter: Option[Highlighter],
    hinter: Option[Hinter],
) extends Cli {
  private val repl = replxx_init()

  val completionHandle = completer.map(ReplxxCli.completionCallbacks.add)
  val highlightHandle = highlighter.map(ReplxxCli.highlightCallbacks.add)
  val hinterHandle = hinter.map(ReplxxCli.hinterCallbacks.add)

  completionHandle.foreach { handle =>
    replxx_set_completion_callback(
      repl,
      (
          input: CString,
          replxxCompletions: replxx_completions,
          contextLen: Ptr[CInt],
          handlePtr: Ptr[Byte],
      ) => {
        val completer = ReplxxCli.completionCallbacks.get(handlePtr).get
        for (completion <- completer.complete(fromCString(input))) {
          replxx_add_completion(
            replxxCompletions,
            ReplxxCli.toCStringNoZone(completion),
          )
        }
      },
      handle,
    )
  }

  highlightHandle.foreach { handle =>
    replxx_set_highlighter_callback(
      repl,
      (
          input: CString,
          colors: Ptr[ReplxxColor],
          size: CInt,
          handlePtr: Ptr[Byte],
      ) => {
        val highlighter = ReplxxCli.highlightCallbacks.get(handlePtr).get
        val highlighted = highlighter.highlight(fromCString(input))
        for (i <- 0 until size) {
          colors(i) = ReplxxCli.fansiStateToReplxxColor(highlighted.getColor(i))
        }
      },
      handle,
    )
  }

  hinterHandle.foreach { handle =>
    replxx_set_hint_callback(
      repl,
      (
          input: CString,
          hints: replxx_hints,
          contextLen: Ptr[CInt],
          color: Ptr[ReplxxColor],
          handlePtr: Ptr[Byte],
      ) => {
        val hinter = ReplxxCli.hinterCallbacks.get(handlePtr).get
        !color = ReplxxCli.fansiColorToReplxxColor(hinter.color)
        val line = fromCString(input)
        for (hint <- hinter.hint(line)) {
          // `line + hint` because replxx expects the hint to start with the line
          replxx_add_hint(hints, ReplxxCli.toCStringNoZone(line + hint))
        }
      },
      handle,
    )
  }

  override def readLine(prompt: String): Option[String] =
    Zone { implicit z =>
      val line = fromCString(replxx_input(repl, toCString(prompt)))
      if (line == null) {
        this.close()
        None
      } else {
        Some(line)
      }
    }

  private def close(): Unit = {
    replxx_end(repl)

    completionHandle.foreach(ReplxxCli.completionCallbacks.remove)
    highlightHandle.foreach(ReplxxCli.highlightCallbacks.remove)
    hinterHandle.foreach(ReplxxCli.hinterCallbacks.remove)
  }
}

object ReplxxCli {
  private val completionCallbacks = new Callbacks[Completer]
  private val highlightCallbacks = new Callbacks[Highlighter]
  private val hinterCallbacks = new Callbacks[Hinter]

  private val colorMap = Map[fansi.Attr, ReplxxColor](
    fansi.Color.Black -> 0,
    fansi.Color.Red -> 1,
    fansi.Color.Green -> 2,
    // fansi.Color.Brown -> 3,
    fansi.Color.Blue -> 4,
    fansi.Color.Magenta -> 5,
    fansi.Color.Cyan -> 6,
    fansi.Color.LightGray -> 7,
    fansi.Color.DarkGray -> 8,
    fansi.Color.LightRed -> 9,
    fansi.Color.LightGreen -> 10,
    fansi.Color.Yellow -> 11,
    fansi.Color.LightBlue -> 12,
    fansi.Color.LightMagenta -> 13,
    fansi.Color.LightCyan -> 14,
    fansi.Color.White -> 15,
    fansi.Color.Reset -> (1 << 16),
  )

  private def fansiColorToReplxxColor(fansiColor: fansi.Attr): ReplxxColor =
    colorMap.getOrElse(fansiColor, 1 << 16)

  private def fansiStateToReplxxColor(
      fansiState: fansi.Str.State
  ): ReplxxColor = fansiColorToReplxxColor(fansi.Color.lookupAttr(fansiState))

  /** Convert a Scala String to a `CString` that won't be freed by the Scala
    * Native GC (so that replxx can access it)
    */
  private def toCStringNoZone(str: String): CString = {
    val bytes = str.getBytes()
    val buf = calloc(bytes.length.toUInt, sizeof[CChar])
    for (i <- 0 until bytes.length) {
      buf(i) = bytes(i)
    }
    buf
  }

  /** An object to store callbacks
    *
    * Since we can't pass proper closures to C, we have to store the hooks in
    * global objects
    */
  private class Callbacks[T] {
    private val callbacks = mutable.Map.empty[Int, T]

    /** Register a callback and get a pointer to a handle to it for accessing
      * the callback later
      */
    def add(callback: T): Ptr[Byte] = {
      val handle = callbacks.size
      callbacks(handle) = callback
      val handlePtr = malloc(sizeof[Int])
      !(handlePtr.asInstanceOf[Ptr[Int]]) = handle
      handlePtr
    }

    def get(handlePtr: Ptr[Byte]): Option[T] =
      callbacks.get(!(handlePtr.asInstanceOf[Ptr[Int]]))

    def remove(handlePtr: Ptr[Byte]): Option[T] = {
      val handle = !handlePtr
      free(handlePtr)
      callbacks.remove(handle)
    }
  }
}
