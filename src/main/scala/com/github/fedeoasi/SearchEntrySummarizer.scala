package com.github.fedeoasi

import java.nio.file.Paths
import java.time.Instant

object SearchEntrySummarizer {
  case class RankedQuery(query: String, rank: Long, minTimestamp: Instant, maxTimestamp: Instant)

  def rank(entries: Seq[SearchEntry]): Seq[RankedQuery] = {
    val rankedQueries = entries.groupBy(_.query).map {
      case (query, entriesForQuery) =>
        val minTimestamp = entriesForQuery.minBy(_.timestamp).timestamp
        val maxTimestamp = entriesForQuery.maxBy(_.timestamp).timestamp
        RankedQuery(query, entriesForQuery.size, minTimestamp, maxTimestamp)
    }
    rankedQueries.toSeq
      .sortBy(_.rank)
      .reverse
  }

  def summary(entries: Seq[SearchEntry]): String = {
    val sb = new StringBuffer()
    sb.append(s"Found ${entries.size} searches\n")
    val rankedQueries = rank(entries)
    sb.append(s"Found ${rankedQueries.size} distinct searches\n")
    val k = 10
    val topKQueries = rankedQueries.take(k)
    sb.append(s"Top $k queries\n")
    sb.append(s"${topKQueries.mkString("\n")}\n")
    sb.toString
  }

  def printRankedQueries(entries: Seq[SearchEntry]): Unit = {
    val header = Seq("Query", "Count", "FirstOccurred", "LastOccurred")
    val rows = rank(entries).map { re => Seq(re.query, re.rank, re.minTimestamp, re.maxTimestamp) }
    CsvHelpers.writeCsv(Seq(header) ++ rows, Paths.get("queries.txt"))
  }

  def main(args: Array[String]): Unit = {
    val entries = SearchDeserializer.parse(Paths.get(args(0)))
    println(summary(entries))
    printRankedQueries(entries)
  }
}
