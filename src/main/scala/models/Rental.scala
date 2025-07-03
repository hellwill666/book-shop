package com.example.models

import java.time.LocalDateTime
import java.util.UUID

case class Rental(
  id: String = UUID.randomUUID().toString,
  bookId: String,
  guestId: String,
  tookAt: LocalDateTime,
  shouldReturn: LocalDateTime, // Always tookAt + 1 week
  returned: Boolean = false
)

object Rental {
  def create(bookId: String, guestId: String, tookAt: LocalDateTime = LocalDateTime.now()): Rental = {
    val shouldReturn = tookAt.plusWeeks(1)
    Rental(
      bookId = bookId,
      guestId = guestId,
      tookAt = tookAt,
      shouldReturn = shouldReturn
    )
  }
}
