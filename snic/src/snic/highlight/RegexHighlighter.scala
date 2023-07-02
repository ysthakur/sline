package snic.highlight

import scala.collection.mutable.ListBuffer
import scala.io.AnsiColor
import scala.util.matching.Regex

case class RegexHighlighter(colors: Iterable[(Regex, String)])
    extends Highlighter {
  override def highlight(line: String) = {
    System.out.flush()
    val overlays = colors.flatMap { case (regex, ansi) =>
      regex.findAllMatchIn(line).map { m =>
        HighlightRange(m.start, m.end, ansi)
      }
    }

    val res = ListBuffer[HighlightRange]()

    for (newRange @ HighlightRange(start, end, ansi) <- overlays) {
      if (res.isEmpty) { res.append(newRange) }
      val ind = res.indexWhere(range => start < range.end)
      if (ind == -1) {
        // Comes after everything else
        res.append(HighlightRange(start, end, ansi))
      } else if (end <= res(ind).start) {
        // Comes before it with no overlap
        res.insert(ind, newRange)
      } else {
        val firstRange = res(ind)

        res.insert(ind, newRange)
        if (firstRange.start < start) {
          res.insert(
            ind,
            HighlightRange(firstRange.start, start, firstRange.ansi),
          )
        }

        var i = if (firstRange.start < start) ind + 2 else ind + 1
        var shouldContinue = true

        while (i < res.length && shouldContinue)
          if (res(i).end < end) res.remove(i)
          else if (end <= res(i).start) shouldContinue = false
          else if (end <= res(i).end) {
            res(i) = HighlightRange(end, res(i).end, res(i).ansi)
            shouldContinue = false
          } else { i += 1 }
      }
    }

    res.toList
  }
}
