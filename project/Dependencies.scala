import sbt._

object Dependencies {
  lazy val arm = "com.jsuereth" %% "scala-arm" % "2.0"
  lazy val json4s = "org.json4s" %% "json4s-jackson" % "3.5.3"
  lazy val scalaCsv = "com.github.tototoshi" %% "scala-csv" % "1.3.5"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  lazy val scopt = "com.github.scopt" %% "scopt" % "3.7.0"
}
