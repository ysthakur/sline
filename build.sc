import $ivy.`com.github.lolgab::mill-crossplatform::0.2.3`
import com.github.lolgab.mill.crossplatform._
import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalanativelib._
import os.{/, GlobSyntax}

val scalaVersions = Seq("2.13.11", "3.3.0")

trait Common extends ScalaModule with ScalafmtModule {
  def scalacOptions =
    Seq(
      "-deprecation",
      "-encoding",
      "utf-8",
      "-feature",
      "-unchecked",
      // Above options from https://tpolecat.github.io/2017/04/25/scalac-flags.html
      "-Xfatal-warnings",
    ) ++
      (if (scalaVersion().startsWith("3"))
         Seq(
           // "-explain",
           "-print-lines"
         )
       else
         Seq("-Xsource:3"))

  def ivyDeps = Agg(ivy"com.outr::scribe::3.11.5")
}

trait CommonNative extends ScalaNativeModule {
  def scalaNativeVersion = "0.4.14"

  def nativeOptimize = false
}

object sline extends Cross[SlineModule](scalaVersions)
trait SlineModule extends CrossPlatform {
  trait Shared extends CrossPlatformCrossScalaModule with Common

  object jvm extends Shared {
    object test extends ScalaTests with SlineTestModule
  }

  object native extends Shared with CommonNative {
    /** Copy everything from replxx's src folder to resources/scala-native */
    def updateReplxx = T.sources {
      val resourcesFolder = millSourcePath / "resources" / "scala-native"
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
        )
        .call()
      def removeXX(file: os.Path): Unit = os
        .write
        .over(
          file,
          os.read(file).replace(".cxx", ".cpp").replace(".hxx", ".hpp"),
        )
      os.walk(replxxFolder / "src")
        .collect {
          os.move
            .matching {
              case _ / g"$file.cxx" =>
                resourcesFolder / g"$file.cpp"
              case _ / g"$file.hxx" =>
                resourcesFolder / g"$file.hpp"
              case _ / g"$file" =>
                resourcesFolder / g"$file"
            }
        }
      os.walk(replxxFolder / "include")
        .collect {
          os.move
            .matching {
              case _ / g"$file.hxx" =>
                resourcesFolder / g"$file.hpp"
              case _ / g"$file" =>
                resourcesFolder / g"$file"
            }
        }
      os.remove.all(replxxFolder)
      os.walk(resourcesFolder)
        .foreach { file =>
          removeXX(file)
        }
      Seq(PathRef(resourcesFolder))
    }

    object test extends ScalaNativeTests with SlineTestModule
  }

  trait SlineTestModule extends TestModule.ScalaTest {
    def defaultCommandName() = "testQuiet"

    def ivyDeps = T(
      super.ivyDeps() ++ Seq(ivy"org.scalatest::scalatest::3.2.16")
    )

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

object demo extends Cross[DemoModule](scalaVersions)
trait DemoModule extends CrossPlatform {
  def moduleDeps = Seq(sline())

  trait Shared extends CrossPlatformCrossScalaModule with Common

  object jvm extends Shared

  object native extends Shared with CommonNative
}
