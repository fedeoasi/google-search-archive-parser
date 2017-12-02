import Dependencies._
import scalariform.formatter.preferences._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.fedeoasi",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "google-search-archive-parser",
    libraryDependencies ++= Seq(
      arm,
      json4s,
      scalaCsv,
      scalaTest % Test),
    scalariformPreferences := scalariformPreferences.value
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(DanglingCloseParenthesis, Preserve)
  )
