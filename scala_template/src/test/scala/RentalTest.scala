package com.example

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.example.models.{Book, Guest, Rental}
import com.example.services.RentalService
import com.example.repositories.{InMemoryBookRepository, InMemoryGuestRepository, InMemoryRentalRepository}
import java.time.LocalDateTime

class RentalTest extends AnyFunSuite with Matchers {

  def createServices() = {
    val bookRepo = new InMemoryBookRepository()
    val guestRepo = new InMemoryGuestRepository()
    val rentalRepo = new InMemoryRentalRepository()
    (bookRepo, guestRepo, rentalRepo, new RentalService(rentalRepo, bookRepo, guestRepo))
  }

  test("rental should be created with return date one week later") {
    val (bookRepo, guestRepo, rentalRepo, rentalService) = createServices()
    
    val book = Book.create("Test Book")
    val guest = Guest.create("Test Guest")
    bookRepo.create(book)
    guestRepo.create(guest)

    val result = rentalService.rentBook(book.id, guest.id)
    result shouldBe a[Right[_, _]]

    val rental = result.toOption.get
    rental.bookId shouldEqual book.id
    rental.guestId shouldEqual guest.id
    rental.returned shouldEqual false
    rental.shouldReturn shouldEqual rental.tookAt.plusWeeks(1)
  }

  test("cannot rent same book twice") {
    val (bookRepo, guestRepo, rentalRepo, rentalService) = createServices()
    
    val book = Book.create("Test Book")
    val guest1 = Guest.create("Test Guest 1")
    val guest2 = Guest.create("Test Guest 2")
    
    bookRepo.create(book)
    guestRepo.create(guest1)
    guestRepo.create(guest2)

    rentalService.rentBook(book.id, guest1.id) shouldBe a[Right[_, _]]
    val secondRental = rentalService.rentBook(book.id, guest2.id)
    secondRental shouldBe a[Left[_, _]]
    secondRental.swap.toOption.get should include("already rented")
  }

  test("can rent book after it's returned") {
    val (bookRepo, guestRepo, rentalRepo, rentalService) = createServices()
    
    val book = Book.create("Test Book")
    val guest1 = Guest.create("Test Guest 1")
    val guest2 = Guest.create("Test Guest 2")
    
    bookRepo.create(book)
    guestRepo.create(guest1)
    guestRepo.create(guest2)

    val firstRental = rentalService.rentBook(book.id, guest1.id).toOption.get
    rentalService.returnBook(firstRental.id) shouldBe a[Right[_, _]]
    rentalService.rentBook(book.id, guest2.id) shouldBe a[Right[_, _]]
  }

  test("return book should set returned flag to true") {
    val (bookRepo, guestRepo, rentalRepo, rentalService) = createServices()
    
    val book = Book.create("Test Book")
    val guest = Guest.create("Test Guest")
    bookRepo.create(book)
    guestRepo.create(guest)

    val rental = rentalService.rentBook(book.id, guest.id).toOption.get
    rental.returned shouldEqual false

    val returnedRental = rentalService.returnBook(rental.id).toOption.get
    returnedRental.returned shouldEqual true
  }

  test("get overdue rentals should return rentals past due date") {
    val (bookRepo, guestRepo, rentalRepo, rentalService) = createServices()
    
    val book = Book.create("Test Book")
    val guest = Guest.create("Test Guest")
    bookRepo.create(book)
    guestRepo.create(guest)

    // Create a rental with past date
    val pastDate = LocalDateTime.now().minusWeeks(2)
    val overdueRental = Rental.create(book.id, guest.id, pastDate)
    rentalRepo.create(overdueRental)

    val overdueRentals = rentalService.getOverdueRentals()
    overdueRentals should have size 1
    overdueRentals.head.id shouldEqual overdueRental.id
  }

  test("get available books should return books not currently rented") {
    val (bookRepo, guestRepo, rentalRepo, rentalService) = createServices()
    
    val book1 = Book.create("Available Book")
    val book2 = Book.create("Rented Book")
    val guest = Guest.create("Test Guest")
    
    bookRepo.create(book1)
    bookRepo.create(book2)
    guestRepo.create(guest)

    rentalService.rentBook(book2.id, guest.id)

    val availableBooks = rentalService.getAvailableBooks()
    availableBooks should have size 1
    availableBooks.head.id shouldEqual book1.id
  }
}
