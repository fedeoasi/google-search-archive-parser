package com.github.fedeoasi

import java.nio.file.Paths

object SearchMain {
  def main(args: Array[String]): Unit = {
    val entries = SearchDeserializer.parse(Paths.get(args(0)))
    SearchEntryPersistence.writeIncrementally(entries, Paths.get("all-queries.csv"))
  }
}
