import mill._
import mill.scalalib._
import mill.scalanativelib._

object snic extends Cross[SnicModule]("2.13", "3.3.0")

trait SnicModule extends Cross.Module[String] with ScalaNativeModule {
  def scalaVersion = crossValue
  def scalaNativeVersion = "0.4.14"

  def suffix = T { "_" + crossValue }
  def bigSuffix = T { "[[[" + suffix() + "]]]" }

  def sources = T.sources(millSourcePath)
}
