import sbt._
import Dependencies._
import sbtbuildinfo.BuildInfoPlugin.autoImport._


name := "Daedalus"

version := "0.1"

scalaVersion := "2.11.8"


//maintainer := "Mandar Chandorkar <mandar2812@gmail.com>"

val baseSettings = Seq(
  organization := "io.github.mandar2812",
  scalaVersion := scala,
  resolvers ++= Seq(
    "jitpack" at "https://jitpack.io",
    "jzy3d-releases" at "http://maven.jzy3d.org/releases",
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases")
)

lazy val commonSettings = Seq(
  libraryDependencies ++= (baseDependencies ++ loggingDependency)
)

lazy val daedalus = (project in file("."))
  .enablePlugins(JavaAppPackaging, BuildInfoPlugin)
  .settings(baseSettings:_*)
  .settings(commonSettings:_*)