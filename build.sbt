Global / onChangedBuildSource := ReloadOnSourceChanges
logLevel := Level.Info

enablePlugins(ScalaNativePlugin)

lazy val scala213 = "2.13.11"
lazy val scala3 = "3.3.0"

ThisBuild / organization := "com.ysthakur"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := scala3

import scala.scalanative.build._

nativeConfig ~= { c => c.withLTO(LTO.none).withMode(Mode.debug) }

val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding",
  "utf-8",
  "-feature",
  "-unchecked",
  // Above options from https://tpolecat.github.io/2017/04/25/scalac-flags.html
  "-Xfatal-warnings",
)

lazy val snic = project.in(file(".")).settings(
  crossScalaVersions := List(scala213, scala3),
  libraryDependencies ++= Seq("com.outr" %%% "scribe" % "3.11.5"),
  Compile / scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) => Seq("-Xsource:3")
      case _ => Seq(
          // "-explain",
          "-print-lines"
        )
    }
  },
)

lazy val demo = project.in(file("demo")).settings().dependsOn(snic)
