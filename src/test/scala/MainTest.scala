package com.example

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.example.services.{BookService, GuestService, RentalService}
import com.example.repositories.{InMemoryBookRepository, InMemoryGuestRepository, InMemoryRentalRepository}

class MainTest extends AnyFunSuite with Matchers {
  
  test("Main should initialize services correctly") {
    // Test that we can create all the services without errors
    val bookRepository = new InMemoryBookRepository()
    val guestRepository = new InMemoryGuestRepository()
    val rentalRepository = new InMemoryRentalRepository()

    val bookService = new BookService(bookRepository)
    val guestService = new GuestService(guestRepository)
    val rentalService = new RentalService(rentalRepository, bookRepository, guestRepository)

    // Services should be initialized
    bookService should not be null
    guestService should not be null
    rentalService should not be null
  }

  test("Integration test - complete rental workflow") {
    // Initialize services
    val bookRepository = new InMemoryBookRepository()
    val guestRepository = new InMemoryGuestRepository()
    val rentalRepository = new InMemoryRentalRepository()

    val bookService = new BookService(bookRepository)
    val guestService = new GuestService(guestRepository)
    val rentalService = new RentalService(rentalRepository, bookRepository, guestRepository)

    // Create a book and guest
    val book = bookService.createBook("Integration Test Book")
    val guest = guestService.createGuest("Integration Test Guest")

    // Rent the book
    val rentalResult = rentalService.rentBook(book.id, guest.id)
    rentalResult shouldBe a[Right[_, _]]
    val rental = rentalResult.toOption.get

    // Verify the rental
    rental.bookId shouldBe book.id
    rental.guestId shouldBe guest.id
    rental.returned shouldBe false

    // Check that book is no longer available
    val availableBooks = rentalService.getAvailableBooks()
    availableBooks should not contain book

    // Return the book
    val returnResult = rentalService.returnBook(rental.id)
    returnResult shouldBe a[Right[_, _]]
    val returnedRental = returnResult.toOption.get
    returnedRental.returned shouldBe true

    // Check that book is available again
    val availableBooksAfterReturn = rentalService.getAvailableBooks()
    availableBooksAfterReturn should contain(book)
  }
}
