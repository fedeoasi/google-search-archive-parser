package com.github

import java.time.Instant

package object fedeoasi {
  case class SearchEntry(query: String, timestamp: Instant)
}
