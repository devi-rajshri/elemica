name := """elemica"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  "org.sorm-framework" % "sorm" % "0.3.18",
  cache,
  ws
)


fork in run := true