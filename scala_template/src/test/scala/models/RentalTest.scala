package com.example.models

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import java.time.LocalDateTime

class RentalTest extends AnyFunSuite with Matchers {

  test("Rental should be created with auto return date") {
    val tookAt = LocalDateTime.now()
    val rental = Rental.create("book-id", "guest-id", tookAt)
    
    rental.bookId shouldBe "book-id"
    rental.guestId shouldBe "guest-id"
    rental.tookAt shouldBe tookAt
    rental.shouldReturn shouldBe tookAt.plusWeeks(1)
    rental.returned shouldBe false
    rental.id should not be empty
  }

  test("Rental should default to current time if no tookAt provided") {
    val beforeCreation = LocalDateTime.now()
    val rental = Rental.create("book-id", "guest-id")
    val afterCreation = LocalDateTime.now()
    
    rental.tookAt should (be.after(beforeCreation.minusSeconds(1)) and be.before(afterCreation.plusSeconds(1)))
    rental.shouldReturn shouldBe rental.tookAt.plusWeeks(1)
    rental.returned shouldBe false
  }

  test("Rental should allow custom parameters") {
    val tookAt = LocalDateTime.now()
    val shouldReturn = tookAt.plusWeeks(1)
    val rental = Rental(
      id = "rental-id",
      bookId = "book-id",
      guestId = "guest-id",
      tookAt = tookAt,
      shouldReturn = shouldReturn,
      returned = true
    )
    
    rental.id shouldBe "rental-id"
    rental.returned shouldBe true
  }
}
