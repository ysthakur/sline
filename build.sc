import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalanativelib._
import mill.scalanativelib.api._
import os.{/, GlobSyntax}

object Defs {
  def scala3Version = "3.3.0"
}

trait SharedSettings extends ScalaNativeModule with ScalafmtModule {
  override def scalaNativeVersion = "0.4.14"

  def nativeOptimize = false

  def scalacOptions = Seq(
    "-deprecation",
    "-encoding",
    "utf-8",
    "-feature",
    "-unchecked",
    // Above options from https://tpolecat.github.io/2017/04/25/scalac-flags.html
    "-Xfatal-warnings",
  ) ++
    (if (scalaVersion().startsWith("3")) Seq(
       // "-explain",
       "-print-lines"
     )
     else Seq("-Xsource:3"))

  def ivyDeps = Agg(ivy"com.outr::scribe::3.11.5")
}

object snline extends Cross[SnlineModule]("2.13.11", Defs.scala3Version)

trait SnlineModule extends Cross.Module[String] with SharedSettings {
  def scalaVersion = crossValue
  def suffix = T("_" + crossValue)
  def bigSuffix = T("[[[" + suffix() + "]]]")

  /** Copy everything from replxx's src folder to resources/scala-native */
  def updateReplxx = T.sources {
    val resourcesFolder = build.millSourcePath / "snline" / "resources" /
      "scala-native"
    os.remove.all(resourcesFolder)
    val replxxFolder = resourcesFolder / "replxx"
    os.makeDir.all(replxxFolder)
    os.proc(
      "git",
      "clone",
      "--depth",
      "1",
      "git@github.com:AmokHuginnsson/replxx.git",
      replxxFolder,
    ).call()
    os.walk(replxxFolder / "src").collect {
      os.move.matching { case _ / g"$file" => resourcesFolder / g"$file" }
    }
    os.remove.all(replxxFolder)
    Seq(PathRef(resourcesFolder))
  }

  object test
      extends TestModule.ScalaTest with ScalaNativeTests with SharedSettings {
    override def scalaNativeVersion = super[SharedSettings].scalaNativeVersion

    def defaultCommandName() = "testQuiet"

    def ivyDeps =
      T(super.ivyDeps() ++ Seq(ivy"org.scalatest::scalatest::3.2.16"))

    /** Like testOnly, but suppresses the output of failed tests */
    def testQuiet(args: String*) = T.command {
      testOnly(
        (if (args.contains("--")) args else args :+ "--") :+ "-oNCXEOPQRM": _*
      )()
    }
  }
}

object demo extends SharedSettings {
  def scalaVersion = Defs.scala3Version

  def moduleDeps = Seq(snline(Defs.scala3Version))
}
