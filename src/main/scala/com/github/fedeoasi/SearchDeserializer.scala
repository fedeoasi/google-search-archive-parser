package com.github.fedeoasi

import java.nio.file.Path
import java.time.Instant

import org.json4s.jackson.Serialization.read

import scala.io.Source

case class SearchFile(event: Seq[Event])
case class Event(query: Query)
case class Query(query_text: String, id: Seq[TimestampHolder])
case class TimestampHolder(timestamp_usec: String)

object SearchDeserializer {
  implicit val formats = org.json4s.DefaultFormats

  def parse(dir: Path): Seq[SearchEntry] = {
    val files = dir.toFile.listFiles().filter(_.getName.endsWith(".json"))
    files.toSeq.flatMap { file =>
      println(s"Reading file $file")
      val json = Source.fromFile(file).getLines().mkString("\n")
      read[SearchFile](json).event.map { e =>
        val query = e.query.query_text
        val timestampLong = e.query.id.head.timestamp_usec.toLong / 1000
        val timestamp = Instant.ofEpochMilli(timestampLong)
        SearchEntry(query, timestamp)
      }
    }
  }
}
