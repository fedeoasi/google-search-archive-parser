package com.github.fedeoasi

import java.nio.file.Paths
import java.time.Instant

import org.scalatest.{ FunSpec, Matchers }

class SearchDeserializerTest extends FunSpec with Matchers {
  private val directory = Paths.get("src/test/resources/sample_dir")

  it("deserializes searches from a directory") {
    SearchDeserializer.parse(directory) shouldBe Seq(
      SearchEntry("first query", Instant.parse("2009-09-03T13:34:17.580Z")),
      SearchEntry("second query", Instant.parse("2009-09-17T14:25:36.140Z")),
      SearchEntry("third query", Instant.parse("2009-09-18T08:12:58.475Z"))
    )
  }
}
