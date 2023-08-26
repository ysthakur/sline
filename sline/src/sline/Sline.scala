package sline

case class Sline(
    val history: History,
    val highlighter: Highlighter,
    val completer: Completer,
)
