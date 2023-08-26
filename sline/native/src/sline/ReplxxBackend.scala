package sline

import scala.collection.mutable
import scala.scalanative.unsafe.*

import sline.facade.replxx.*

class ReplxxBackend(private val completer: Completer) extends Backend {

  private val repl = replxx_init()

  // Since we can't pass proper closures to C, we have to store the completers
  // in a global object
  val completionHandle = {
    val handle = ReplxxBackend.completionCallbacks.size
    ReplxxBackend.completionCallbacks(handle) = completer
    handle
  }
  replxx_set_completion_callback(
    repl,
    (
        input: CString,
        replxxCompletions: replxx_completions,
        contextLen: Ptr[Int],
        handlePtr: Ptr[Byte],
    ) =>
      Zone { implicit z =>
        val completionHandle = !(handlePtr.asInstanceOf[Ptr[Int]])
        val completer = ReplxxBackend.completionCallbacks(completionHandle)
        for (completion <- completer.complete(fromCString(input))) {
          replxx_add_completion(replxxCompletions, toCString(completion))
        }
      },
    completionHandle.toPtr,
  )

  def setPrompt(prompt: String): Unit = Zone { implicit z =>
    replxx_set_prompt(repl, toCString(prompt))
  }

  def readLine(prompt: String): String = Zone { implicit z =>
    fromCString(replxx_input(replxx, toCString(prompt)))
  }

  def close(): Unit = Zone { implicit z =>
    replxx_end(repl)

    ReplxxBackend.completionCallbacks.remove(completionHandle)
  }
}

object ReplxxBackend {
  private val completionCallbacks = mutable.Map.empty[Int, Completer]
}
