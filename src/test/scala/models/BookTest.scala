package com.example.models

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import java.time.LocalDateTime

class BookTest extends AnyFunSuite with Matchers {

  test("Book should be created with default cost") {
    val book = Book.create("Test Book")
    
    book.name shouldBe "Test Book"
    book.cost shouldBe 10.0
    book.id should not be empty
  }

  test("Book should be created with custom parameters") {
    val book = Book(id = "test-id", name = "Custom Book", cost = 15.0)
    
    book.id shouldBe "test-id"
    book.name shouldBe "Custom Book"
    book.cost shouldBe 15.0
  }
}
