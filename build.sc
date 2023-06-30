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

  def nativeOptimize = false

  def scalacOptions =
    Seq(
      "-deprecation",
      "-encoding",
      "utf-8",
      "-feature",
      "-unchecked",
      // Above options from https://tpolecat.github.io/2017/04/25/scalac-flags.html
      "-Xfatal-warnings"
    ) ++
      (if (scalaVersion().startsWith("3"))
         Seq(
           // "-explain",
           "-print-lines"
         )
       else
         Seq())

  def ivyDeps = Agg(
    ivy"com.outr::scribe::3.11.5",
    ivy"com.lihaoyi::fansi::0.4.0"
  )
}

object snic extends Cross[SnicModule]("2.13.11", Defs.scala3Version)

trait SnicModule extends Cross.Module[String] with SharedSettings {
  def scalaVersion = crossValue
  def suffix = T {
    "_" + crossValue
  }
  def bigSuffix = T {
    "[[[" + suffix() + "]]]"
  }

  object test
      extends TestModule.ScalaTest
      with ScalaNativeTests
      with SharedSettings {
    override def scalaNativeVersion = super[SharedSettings].scalaNativeVersion

    def defaultCommandName() = "testQuiet"

    def ivyDeps = T {
      super.ivyDeps() ++ Seq(ivy"org.scalatest::scalatest::3.2.16")
    }

    /** Like testOnly, but suppresses the output of failed tests */
    def testQuiet(args: String*) = T.command {
      testOnly(
        (if (args.contains("--"))
           args
         else
           args :+ "--") :+ "-oNCXEOPQRM": _*
      )()
    }
  }
}

object demo extends SharedSettings {
  def scalaVersion = Defs.scala3Version

  def moduleDeps = Seq(snic(Defs.scala3Version))

  /** The default isn't interactive */
  override def run(args: Task[Args] = T.task(Args())) = T.command {
    os.proc(nativeLink().toString, args().value.toSeq)
      .call(stdin = os.Inherit, stdout = os.Inherit, stderr = os.Inherit)
    mill.api.Result.Success(())
  }
}
