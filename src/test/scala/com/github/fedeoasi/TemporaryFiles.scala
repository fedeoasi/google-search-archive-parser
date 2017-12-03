package com.github.fedeoasi

import java.nio.file.{ Files, Path }

trait TemporaryFiles {
  def withTmpFile[T](prefix: String, suffix: String)(f: Path => T): T = {
    val tmpFile = Files.createTempFile("queries", "csv")
    try {
      f(tmpFile)
    } finally {
      tmpFile.toFile.delete()
    }
  }
}
