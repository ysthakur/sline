import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalanativelib._
import mill.scalanativelib.api._

object Defs {
  def scala3Version = "3.3.0"
}

trait SharedSettings extends ScalaNativeModule with ScalafmtModule {
  override def scalaNativeVersion = "0.4.14"

  def releaseMode = ReleaseMode.ReleaseFast
  def nativeLTO = LTO.Thin

  def scalacOptions = Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    // Above options from https://tpolecat.github.io/2017/04/25/scalac-flags.html
    "-Xfatal-warnings",
    // "-explain",
    "-print-lines"
  )
}

object snic extends Cross[SnicModule]("2.13", Defs.scala3Version)

trait SnicModule extends Cross.Module[String] with SharedSettings {
  def scalaVersion = crossValue
  def suffix = T { "_" + crossValue }
  def bigSuffix = T { "[[[" + suffix() + "]]]" }

  // def sources = T.sources(millSourcePath)

  object test extends TestModule.ScalaTest with ScalaNativeTests with SharedSettings {
    override def scalaNativeVersion = super[SharedSettings].scalaNativeVersion

    def defaultCommandName() = "testQuiet"

    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest::3.2.16"
    )

    /** Like testOnly, but suppresses the output of failed tests */
    def testQuiet(args: String*) = T.command {
      testOnly((if (args.contains("--")) args else args :+ "--") :+ "-oNCXEOPQRM": _*)()
    }
  }
}

object demo extends SharedSettings {
  def scalaVersion = Defs.scala3Version

  def moduleDeps = Seq(snic(Defs.scala3Version))
}
