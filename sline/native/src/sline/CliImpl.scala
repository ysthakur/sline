package sline

import scala.collection.mutable
import scala.scalanative.libc.stdlib.{calloc, free, malloc}
import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

import sline.{Cli, Completer}
import sline.replxx.*

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
    ) => {
      val completer = CliImpl.completionCallbacks.get(handlePtr).get
      for (completion <- completer.complete(fromCString(input))) {
        val bytes = completion.getBytes()
        val buf = calloc(bytes.length.toUInt, sizeof[CChar])
        for (i <- 0 until bytes.length) {
          buf(i) = bytes(i)
        }
        replxx_add_completion(replxxCompletions, buf)
      }
    },
    completionHandle,
  )

  // replxx_set_highlighter_callback(
  //   repl,
  //   (
  //       input: CString,
  //       colors: Ptr[ReplxxColor],
  //       size: CInt,
  //       handlePtr: Ptr[Byte],
  //   ) =>
  //     Zone { implicit z =>
  //       val highlighter = CliImpl.highlightCallbacks.get(handlePtr).get
  //       val highlighted = highlighter.highlight(fromCString(input))
  //       for (i <- 0 until size) {
  //         colors(i) = CliImpl.fansiToReplxxColor(highlighted.getColor(i))
  //       }
  //     },
  //   highlightHandle,
  // )

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
      CliImpl.highlightCallbacks.remove(highlightHandle)
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
