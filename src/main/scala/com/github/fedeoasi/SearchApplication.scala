package com.github.fedeoasi

import java.io.File
import java.nio.file.{ Path, Paths }

class SearchTestableApplication(val searchCsv: Path) {

  def main(args: Array[String]): Unit = {
    parser.parse(args, Config()) match {
      case Some(Config(Some(searchesFolder))) =>
        val entries = SearchDeserializer.parse(searchesFolder.toPath)
        SearchEntryPersistence.writeIncrementally(entries, searchCsv)
      case _ => throw new RuntimeException("Argument parsing failed")
    }
  }

  private val parser = new scopt.OptionParser[Config]("scopt") {
    head("google-search-archive-parser")

    help("help").text("prints this usage text")

    opt[File]('s', "searches-folder").required().valueName("<folder>")
      .required()
      .action((x, c) => c.copy(searchesFolder = Some(x)))
      .text("folder from the extracted archive that contains json files")
      .validate { file => if (file.isDirectory) Right(()) else Left(s"File $file is not a directory") }
  }

  case class Config(searchesFolder: Option[File] = None)
}

object SearchApplication extends SearchTestableApplication(Paths.get("all-queries.csv"))
