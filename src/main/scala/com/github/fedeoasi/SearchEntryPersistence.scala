package com.github.fedeoasi

import java.nio.file.Path
import java.time.Instant

import com.github.tototoshi.csv.CSVReader
import resource._

import scala.io.Source

object SearchEntryPersistence {
  private val QueryField = "Query"
  private val TimestampField = "Timestamp"

  def read(file: Path): Seq[SearchEntry] = {
    managed(CSVReader.open(file.toFile)).acquireAndGet { reader =>
      reader.allWithHeaders().map { row =>
        SearchEntry(row(QueryField), Instant.parse(row(TimestampField)))
      }
    }
  }

  def write(entries: Seq[SearchEntry], toFile: Path): Unit = {
    val header = Seq(QueryField, TimestampField)
    CsvHelpers.writeCsv(Seq(header) ++ toCsvSeq(entries), toFile)
  }

  def writeIncrementally(entries: Seq[SearchEntry], file: Path): Unit = {
    val existingEntries = if (!file.toFile.exists() || isEmpty(file)) {
      write(Seq.empty, file)
      Seq.empty
    } else {
      read(file)
    }
    incrementalWriteAction(existingEntries, entries) match {
      case Append(entriesToAppend) =>
        val since = entriesToAppend.headOption.map(e => s"since ${e.timestamp}").getOrElse("")
        println(s"Adding ${entriesToAppend.size} search entries $since")
        CsvHelpers.writeCsv(toCsvSeq(entriesToAppend), file, append = true)
      case DoNothing =>
        println(s"No new entries to add")
    }
  }

  private[fedeoasi] def incrementalWriteAction(
    existingEntries: Seq[SearchEntry],
    newEntries: Seq[SearchEntry]): WriteAction = {

    val entriesToAdd = if (existingEntries.nonEmpty) {
      val maxTimestamp = existingEntries.maxBy(_.timestamp).timestamp
      newEntries.filter(_.timestamp.isAfter(maxTimestamp))
    } else {
      newEntries
    }
    if (entriesToAdd.isEmpty) {
      DoNothing
    } else {
      Append(entriesToAdd.sortBy(_.timestamp))
    }
  }

  private def toCsvSeq(entries: Seq[SearchEntry]): Seq[Seq[Any]] = {
    entries.map { re => Seq(re.query, re.timestamp) }
  }

  private def isEmpty(file: Path): Boolean = {
    managed(Source.fromFile(file.toFile)).acquireAndGet { source =>
      Option(source.bufferedReader().readLine()).isEmpty
    }
  }

  sealed trait WriteAction
  case object DoNothing extends WriteAction
  case class Append(entries: Seq[SearchEntry]) extends WriteAction
}
