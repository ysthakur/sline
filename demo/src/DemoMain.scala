import snic.facade.readline

import scalanative.unsafe.*

@main
def main(): Unit = {
  println("foo")
  println(readline.readline(c"foo"))
}
