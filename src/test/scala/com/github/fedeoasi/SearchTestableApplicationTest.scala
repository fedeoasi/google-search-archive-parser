package com.github.fedeoasi

import java.nio.file.Paths

import org.scalatest.{ FunSpec, Matchers }

class SearchTestableApplicationTest extends FunSpec with Matchers with TemporaryFiles {
  private val searchesFolder = Paths.get("src/test/resources/sample_dir")

  it("throws an exception when no argument is provided") {
    a[RuntimeException] shouldBe thrownBy {
      withTmpFile("search", "csv") { tmpFile =>
        new SearchTestableApplication(tmpFile).main(Array.empty)
      }
    }
  }

  it("runs successfully") {
    withTmpFile("search", "csv") { tmpFile =>
      new SearchTestableApplication(tmpFile).main(Array("-s", searchesFolder.toString))
      SearchEntryPersistence.read(tmpFile).size shouldBe 3
    }
  }
}
