package sline

import scala.collection.mutable
import scala.scalanative.unsafe.*

import sline.{Cli, Completer}
import sline.facade.replxx.*

class ReplxxCli(private val completer: Completer) extends Cli {
  private val repl = replxx_init()

  // Since we can't pass proper closures to C, we have to store the completers
  // in a global object
  val completionHandle = {
    val handle = ReplxxCli.completionCallbacks.size
    ReplxxCli.completionCallbacks(handle) = completer
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
        val completer = ReplxxCli.completionCallbacks(completionHandle)
        for (completion <- completer.complete(fromCString(input))) {
          replxx_add_completion(replxxCompletions, toCString(completion))
        }
      },
    completionHandle.toPtr,
  )

  override def readLine(prompt: String): String =
    Zone { implicit z =>
      fromCString(replxx_input(repl, toCString(prompt)))
    }

  override def close(): Unit =
    Zone { implicit z =>
      replxx_end(repl)

      ReplxxCli.completionCallbacks.remove(completionHandle)
    }
}

object ReplxxCli {
  private val completionCallbacks = mutable.Map.empty[Int, Completer]
}
