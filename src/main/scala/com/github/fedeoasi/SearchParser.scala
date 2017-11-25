package com.github.fedeoasi

import java.io.FileOutputStream
import java.nio.file.{Path, Paths}
import java.time.Instant

import scala.io.Source
import org.json4s.jackson.Serialization._

import scala.util.Try

case class SearchFile(event: Seq[Event])
case class Event(query: Query)
case class Query(query_text: String, id: Seq[TimestampHolder])
case class TimestampHolder(timestamp_usec: String)

case class Entry(query: String, timestamp: Instant)

object SearchParser {
  implicit val formats = org.json4s.DefaultFormats

  def parse(dir: Path): Seq[Entry] = {
    val files = dir.toFile.listFiles().filter(_.getName.endsWith(".json"))
    println(files)
    files.toSeq.flatMap { file =>
      println(s"Reading file $file")
      val json = Source.fromFile(file).getLines().mkString("\n")
      read[SearchFile](json).event.map { e =>
        val query = e.query.query_text
        val timestampLong = e.query.id.head.timestamp_usec.toLong / 1000
        val timestamp = Instant.ofEpochMilli(timestampLong)
        Entry(query, timestamp)
      }
    }
  }

  def rank(entries: Seq[Entry]): Seq[RankedQuery] = {
    entries.groupBy(_.query).map { case (query, entries) =>
      val minTimestamp = entries.minBy(_.timestamp).timestamp
      val maxTimestamp = entries.maxBy(_.timestamp).timestamp
      RankedQuery(query, entries.size, minTimestamp, maxTimestamp)
    }
      .toSeq
      .sortBy(_.rank)
      .reverse
  }

  def summary(entries: Seq[Entry]): String = {
    val sb = new StringBuffer()
    sb.append(s"Found ${entries.size} searches\n")
    val rankedQueries = rank(entries)
    sb.append(s"Found ${rankedQueries.size} distinct searches\n")
    val k = 20
    val topKQueries = rankedQueries.take(k)
    sb.append(s"Top $k queries\n")
    sb.append(s"${topKQueries.mkString("\n")}\n")
    sb.toString
  }

  def printToFile(strings: Seq[String], filename: String): Unit = {
    //TODO use arm
    val fos = new FileOutputStream(filename)
    Try {
      strings.mkString("n")
      fos.write(strings.mkString("\n").getBytes())
    }
    fos.close()
  }

  def main(args: Array[String]): Unit = {
    val entries = parse(Paths.get(args(0)))
    println(summary(entries))
    printToFile(rank(entries).map(re => s"${re.query},${re.rank},${re.minTimestamp},${re.maxTimestamp}"), "queries.txt")
  }
}

case class RankedQuery(query: String, rank: Long, minTimestamp: Instant, maxTimestamp: Instant)