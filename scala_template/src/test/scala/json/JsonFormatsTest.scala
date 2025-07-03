package com.example.json

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.example.models.{Book, Guest, Rental}
import spray.json._
import java.time.LocalDateTime
import JsonFormats._

class JsonFormatsTest extends AnyFunSuite with Matchers {

  test("Book should serialize to and from JSON") {
    val book = Book(id = "test-id", name = "Test Book", cost = 15.0)
    
    val json = book.toJson
    val deserializedBook = json.convertTo[Book]
    
    deserializedBook shouldBe book
  }

  test("Guest should serialize to and from JSON") {
    val guest = Guest(id = "guest-id", name = "John Doe")
    
    val json = guest.toJson
    val deserializedGuest = json.convertTo[Guest]
    
    deserializedGuest shouldBe guest
  }

  test("Rental should serialize to and from JSON") {
    val now = LocalDateTime.now()
    val rental = Rental(
      id = "rental-id",
      bookId = "book-id",
      guestId = "guest-id",
      tookAt = now,
      shouldReturn = now.plusWeeks(1),
      returned = false
    )
    
    val json = rental.toJson
    val deserializedRental = json.convertTo[Rental]
    
    deserializedRental shouldBe rental
  }

  test("List of Books should serialize to and from JSON") {
    val books = List(
      Book(id = "1", name = "Book 1", cost = 10.0),
      Book(id = "2", name = "Book 2", cost = 12.0)
    )
    
    val json = books.toJson
    val deserializedBooks = json.convertTo[List[Book]]
    
    deserializedBooks shouldBe books
  }

  test("CreateBookRequest should serialize to and from JSON") {
    val request = CreateBookRequest("New Book")
    
    val json = request.toJson
    val deserializedRequest = json.convertTo[CreateBookRequest]
    
    deserializedRequest shouldBe request
  }

  test("ErrorResponse should serialize to and from JSON") {
    val error = ErrorResponse("Something went wrong")
    
    val json = error.toJson
    val deserializedError = json.convertTo[ErrorResponse]
    
    deserializedError shouldBe error
  }

  test("LocalDateTime should serialize to ISO format") {
    val dateTime = LocalDateTime.of(2024, 12, 25, 15, 30, 0)
    
    val json = dateTime.toJson
    json shouldBe JsString("2024-12-25T15:30:00")
    
    val deserializedDateTime = json.convertTo[LocalDateTime]
    deserializedDateTime shouldBe dateTime
  }
}
