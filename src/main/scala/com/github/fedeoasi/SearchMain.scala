package com.github.fedeoasi

import java.io.FileOutputStream
import java.nio.file.Paths
import java.time.Instant

import resource._

object SearchMain {
  case class RankedQuery(query: String, rank: Long, minTimestamp: Instant, maxTimestamp: Instant)

  def rank(entries: Seq[SearchEntry]): Seq[RankedQuery] = {
    val rankedQueries = entries.groupBy(_.query).map { case (query, entries) =>
      val minTimestamp = entries.minBy(_.timestamp).timestamp
      val maxTimestamp = entries.maxBy(_.timestamp).timestamp
      RankedQuery(query, entries.size, minTimestamp, maxTimestamp)
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
    val k = 20
    val topKQueries = rankedQueries.take(k)
    sb.append(s"Top $k queries\n")
    sb.append(s"${topKQueries.mkString("\n")}\n")
    sb.toString
  }

  def printToFile(strings: Seq[String], filename: String): Unit = {
    managed(new FileOutputStream(filename)).acquireAndGet { fos =>
      fos.write(strings.mkString("\n").getBytes())
    }
  }

  def main(args: Array[String]): Unit = {
    val entries = SearchDeserializer.parse(Paths.get(args(0)))
    println(summary(entries))
    printToFile(rank(entries).map(re => s"${re.query},${re.rank},${re.minTimestamp},${re.maxTimestamp}"), "queries.txt")
  }
}
