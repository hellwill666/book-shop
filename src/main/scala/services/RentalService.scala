package com.example.services

import com.example.models.{Rental, Book, Guest}
import com.example.repositories.{RentalRepository, BookRepository, GuestRepository}
import java.time.LocalDateTime

class RentalService(
  rentalRepository: RentalRepository,
  bookRepository: BookRepository,
  guestRepository: GuestRepository
) {
  
  def rentBook(bookId: String, guestId: String): Either[String, Rental] = {
    // Check if book exists
    bookRepository.findById(bookId) match {
      case None => Left(s"Book with id $bookId not found")
      case Some(_) =>
        // Check if guest exists
        guestRepository.findById(guestId) match {
          case None => Left(s"Guest with id $guestId not found")
          case Some(_) =>
            // Check if book is already rented and not returned
            val activeRentals = rentalRepository.findByBookId(bookId).filter(!_.returned)
            if (activeRentals.nonEmpty) {
              Left(s"Book with id $bookId is already rented")
            } else {
              val rental = Rental.create(bookId, guestId)
              Right(rentalRepository.create(rental))
            }
        }
    }
  }

  def returnBook(rentalId: String): Either[String, Rental] = {
    rentalRepository.findById(rentalId) match {
      case None => Left(s"Rental with id $rentalId not found")
      case Some(rental) =>
        if (rental.returned) {
          Left("Book is already returned")
        } else {
          val updatedRental = rental.copy(returned = true)
          rentalRepository.update(updatedRental) match {
            case Some(updated) => Right(updated)
            case None => Left("Failed to update rental")
          }
        }
    }
  }

  def returnBookByBookId(bookId: String): Either[String, Rental] = {
    val activeRentals = rentalRepository.findByBookId(bookId).filter(!_.returned)
    activeRentals match {
      case Nil => Left(s"No active rental found for book $bookId")
      case rental :: _ => returnBook(rental.id)
    }
  }

  def getRental(id: String): Option[Rental] = {
    rentalRepository.findById(id)
  }

  def getAllRentals(): List[Rental] = {
    rentalRepository.findAll()
  }

  def getActiveRentals(): List[Rental] = {
    rentalRepository.findActiveRentals()
  }

  def getOverdueRentals(): List[Rental] = {
    rentalRepository.findOverdueRentals()
  }

  def getRentalsByGuest(guestId: String): List[Rental] = {
    rentalRepository.findByGuestId(guestId)
  }

  def getRentalsByBook(bookId: String): List[Rental] = {
    rentalRepository.findByBookId(bookId)
  }

  def getAvailableBooks(): List[Book] = {
    val allBooks = bookRepository.findAll()
    val rentedBookIds = rentalRepository.findActiveRentals().map(_.bookId).toSet
    allBooks.filterNot(book => rentedBookIds.contains(book.id))
  }
}
