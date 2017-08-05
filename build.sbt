import sbt._
import sbt.Keys._

lazy val root = (project in file("."))
  .settings(Defaults.itSettings: _*)
  .settings(
    name := "thin-scala",
    organization := "org.opendatakit",
    version := "0.1.0",

    scalaVersion := "2.12.3",
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-nop" % "1.7.25",
      "com.typesafe" % "config" % "1.3.1",
      "com.github.finagle" %% "finch-core" % "0.16.0-M1",
      "com.github.finagle" %% "finch-circe" % "0.16.0-M1",
      "io.circe" %% "circe-generic" % "0.8.0",
      "com.typesafe.slick" %% "slick" % "3.2.1",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
      "org.postgresql" % "postgresql" % "42.1.1"
    )
  )

