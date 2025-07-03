package com.example.services

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.example.models.{Book, Guest, Rental}
import com.example.repositories.{InMemoryBookRepository, InMemoryGuestRepository, InMemoryRentalRepository}
import java.time.LocalDateTime

class RentalServiceTest extends AnyFunSuite with Matchers {

  def createTestServices() = {
    val bookRepo = new InMemoryBookRepository()
    val guestRepo = new InMemoryGuestRepository()
    val rentalRepo = new InMemoryRentalRepository()
    val service = new RentalService(rentalRepo, bookRepo, guestRepo)
    
    val book = Book.create("Test Book")
    val guest = Guest.create("Test Guest")
    bookRepo.create(book)
    guestRepo.create(guest)
    
    (service, book, guest, rentalRepo)
  }

  test("RentalService should rent a book successfully") {
    val (service, book, guest, _) = createTestServices()
    
    val result = service.rentBook(book.id, guest.id)
    
    result shouldBe a[Right[_, _]]
    val rental = result.right.get
    rental.bookId shouldBe book.id
    rental.guestId shouldBe guest.id
    rental.returned shouldBe false
    rental.shouldReturn shouldBe rental.tookAt.plusWeeks(1)
  }

  test("RentalService should not rent non-existent book") {
    val (service, _, guest, _) = createTestServices()
    
    val result = service.rentBook("non-existent-book", guest.id)
    
    result shouldBe a[Left[_, _]]
    result.left.get should include("Book with id non-existent-book not found")
  }

  test("RentalService should not rent to non-existent guest") {
    val (service, book, _, _) = createTestServices()
    
    val result = service.rentBook(book.id, "non-existent-guest")
    
    result shouldBe a[Left[_, _]]
    result.left.get should include("Guest with id non-existent-guest not found")
  }

  test("RentalService should not rent already rented book") {
    val (service, book, guest, _) = createTestServices()
    
    // First rental
    service.rentBook(book.id, guest.id)
    
    // Try to rent the same book again
    val result = service.rentBook(book.id, guest.id)
    
    result shouldBe a[Left[_, _]]
    result.left.get should include("is already rented")
  }

  test("RentalService should return a book successfully") {
    val (service, book, guest, _) = createTestServices()
    
    val rentalResult = service.rentBook(book.id, guest.id)
    val rental = rentalResult.right.get
    
    val returnResult = service.returnBook(rental.id)
    
    returnResult shouldBe a[Right[_, _]]
    val returnedRental = returnResult.right.get
    returnedRental.returned shouldBe true
  }

  test("RentalService should not return non-existent rental") {
    val (service, _, _, _) = createTestServices()
    
    val result = service.returnBook("non-existent-rental")
    
    result shouldBe a[Left[_, _]]
    result.left.get should include("Rental with id non-existent-rental not found")
  }

  test("RentalService should not return already returned book") {
    val (service, book, guest, rentalRepo) = createTestServices()
    
    val rentalResult = service.rentBook(book.id, guest.id)
    val rental = rentalResult.right.get
    
    // Return the book first time
    service.returnBook(rental.id)
    
    // Try to return again
    val result = service.returnBook(rental.id)
    
    result shouldBe a[Left[_, _]]
    result.left.get should include("Book is already returned")
  }

  test("RentalService should return book by book ID") {
    val (service, book, guest, _) = createTestServices()
    
    service.rentBook(book.id, guest.id)
    
    val result = service.returnBookByBookId(book.id)
    
    result shouldBe a[Right[_, _]]
    val returnedRental = result.right.get
    returnedRental.returned shouldBe true
  }

  test("RentalService should get available books") {
    val (service, book, guest, _) = createTestServices()
    
    // Initially all books should be available
    val availableBooks = service.getAvailableBooks()
    availableBooks should contain(book)
    
    // After renting, book should not be available
    service.rentBook(book.id, guest.id)
    val availableAfterRent = service.getAvailableBooks()
    availableAfterRent should not contain book
  }

  test("RentalService should get overdue rentals") {
    val (service, book, guest, rentalRepo) = createTestServices()
    
    // Create an overdue rental
    val pastDate = LocalDateTime.now().minusWeeks(2)
    val overdueRental = Rental.create(book.id, guest.id, pastDate)
    rentalRepo.create(overdueRental)
    
    val overdueRentals = service.getOverdueRentals()
    overdueRentals should contain(overdueRental)
  }
}
