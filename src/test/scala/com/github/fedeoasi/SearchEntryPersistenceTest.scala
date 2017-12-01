package com.github.fedeoasi

import java.time.Instant

import org.scalatest.{ FunSpec, Matchers }
import SearchEntryPersistence._

class SearchEntryPersistenceTest extends FunSpec with Matchers {
  describe("with two search entries") {
    val entries = Seq(
      SearchEntry("a", Instant.parse("2017-01-01T12:00:00.000Z")),
      SearchEntry("b", Instant.parse("2017-01-02T01:00:00.000Z"))
    )

    it("does nothing when adding no entries") {
      incrementalWriteAction(entries, Seq.empty) shouldBe DoNothing
    }

    it("does nothing when adding the same entries") {
      incrementalWriteAction(entries, entries) shouldBe DoNothing
    }

    it("does nothing when adding an older entry") {
      val olderEntry = SearchEntry("c", Instant.parse("2016-12-31T12:00:00.000Z"))
      incrementalWriteAction(entries, Seq(olderEntry)) shouldBe DoNothing
    }

    describe("with a new entry") {
      val newEntry = SearchEntry("c", Instant.parse("2017-02-01T12:00:00.000Z"))

      it("appends a new entry") {
        incrementalWriteAction(entries, Seq(newEntry)) shouldBe Append(Seq(newEntry))
      }

      it("only appends a new entry") {
        incrementalWriteAction(entries, entries ++ Seq(newEntry)) shouldBe Append(Seq(newEntry))
      }

    }
  }
}
