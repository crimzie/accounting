name := """accounting"""
organization := "com.crimzie"
version := "1.0-SNAPSHOT"
scalaVersion := "2.12.4"
scalacOptions += "-Ypartial-unification"

lazy val root = project in file(".") enablePlugins PlayScala

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "io.monix" %% "monix" % "3.0.0-M3",
  "org.typelevel" %% "cats-core" % "1.0.1",
)
