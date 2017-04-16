import sbt._
import Dependencies._


name := "Daedalus"

version := "0.1"

scalaVersion := "2.11.8"


maintainer := "Mandar Chandorkar <mandar2812@gmail.com>"

val baseSettings = Seq(
  organization := "io.github.mandar2812",
  scalaVersion := scala,
  resolvers in ThisBuild ++= Seq(
    "jitpack" at "https://jitpack.io",
    "jzy3d-releases" at "http://maven.jzy3d.org/releases",
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    "BeDataDriven" at "https://nexus.bedatadriven.com/content/groups/public",
    Resolver.sonatypeRepo("public"))
)

lazy val commonSettings = Seq(
  libraryDependencies ++= (baseDependencies ++ loggingDependency),
  initialCommands in console += """io.github.mandar2812.dynaml.DynaML.main(Array())"""
)

lazy val daedalus = (project in file("."))
  .enablePlugins(JavaAppPackaging, BuildInfoPlugin)
  .settings(baseSettings:_*)
  .settings(commonSettings:_*)