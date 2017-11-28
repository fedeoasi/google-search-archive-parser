package com.github.fedeoasi

import java.nio.file.Path

import com.github.tototoshi.csv.CSVWriter
import resource.managed

object CsvHelpers {
  def writeCsv(rows: Seq[Seq[Any]], file: Path, append: Boolean = false): Unit = {
    managed(CSVWriter.open(file.toFile, append = append)).acquireAndGet { writer =>
      writer.writeAll(rows)
    }
  }
}
