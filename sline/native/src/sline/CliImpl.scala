package sline

import scala.collection.mutable
import scala.scalanative.unsafe.*

import sline.{Cli, Completer}
import sline.facade.replxx.*

/** Scala Native implementation of the CLI using replxx */
class CliImpl(completer: Completer, highlighter: Highlighter) extends Cli {
  private val repl = replxx_init()

  val completionHandle = CliImpl.completionCallbacks.add(completer)
  val highlightHandle = CliImpl.highlightCallbacks.add(highlighter)

  replxx_set_completion_callback(
    repl,
    (
        input: CString,
        replxxCompletions: replxx_completions,
        contextLen: Ptr[CInt],
        handlePtr: Ptr[Byte],
    ) =>
      Zone { implicit z =>
        val completer = CliImpl.completionCallbacks.get(handlePtr).get
        for (completion <- completer.complete(fromCString(input))) {
          replxx_add_completion(replxxCompletions, toCString(completion))
        }
      },
    completionHandle.toPtr,
  )

  replxx_set_highlighter_callback(
    repl,
    (
        input: CString,
        colors: Ptr[ReplxxColor],
        size: CInt,
        handlePtr: Ptr[Byte],
    ) =>
      Zone { implicit z =>
        val highlighter = CliImpl.highlightCallbacks.get(handlePtr).get
        val highlighted = highlighter.highlight(fromCString(input))
        for (i <- 0 until size) {
          colors(i) = CliImpl.fansiToReplxxColor(highlighted.getColor(i))
        }
      },
    highlightHandle.toPtr,
  )

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

  private def close(): Unit =
    Zone { implicit z =>
      replxx_end(repl)

      CliImpl.completionCallbacks.remove(completionHandle)
    }
}

object CliImpl {
  private val completionCallbacks = new Callbacks[Completer]

  private val highlightCallbacks = new Callbacks[Highlighter]

  private def fansiToReplxxColor(fansiState: fansi.Str.State): ReplxxColor = ???

  /** An object to store callbacks
    *
    * Since we can't pass proper closures to C, we have to store the hooks in
    * global objects
    */
  private class Callbacks[T] {
    private val callbacks = mutable.Map.empty[Int, T]

    /** Register a callback and get a handle to it for accessing the callback
      * later
      */
    def add(callback: T): Int = {
      val handle = callbacks.size
      callbacks(handle) = callback
      handle
    }

    def get(handle: Int): Option[T] = callbacks.get(handle)

    /** Convenience method to get the handle from a C `void*` */
    def get(handlePtr: Ptr[Byte]): Option[T] =
      callbacks.get(!(handlePtr.asInstanceOf[Ptr[Int]]))

    def remove(handle: Int): Option[T] = callbacks.remove(handle)
  }
}
