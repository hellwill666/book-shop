package com.example.repositories

import com.example.models.Rental
import scala.collection.mutable

trait RentalRepository {
  def create(rental: Rental): Rental
  def findById(id: String): Option[Rental]
  def findAll(): List[Rental]
  def findByBookId(bookId: String): List[Rental]
  def findByGuestId(guestId: String): List[Rental]
  def findActiveRentals(): List[Rental]
  def findOverdueRentals(): List[Rental]
  def update(rental: Rental): Option[Rental]
  def delete(id: String): Boolean
}

class InMemoryRentalRepository extends RentalRepository {
  private val rentals = mutable.Map[String, Rental]()

  override def create(rental: Rental): Rental = {
    rentals(rental.id) = rental
    rental
  }

  override def findById(id: String): Option[Rental] = rentals.get(id)

  override def findAll(): List[Rental] = rentals.values.toList

  override def findByBookId(bookId: String): List[Rental] = 
    rentals.values.filter(_.bookId == bookId).toList

  override def findByGuestId(guestId: String): List[Rental] = 
    rentals.values.filter(_.guestId == guestId).toList

  override def findActiveRentals(): List[Rental] = 
    rentals.values.filter(!_.returned).toList

  override def findOverdueRentals(): List[Rental] = {
    val now = java.time.LocalDateTime.now()
    rentals.values.filter(r => !r.returned && r.shouldReturn.isBefore(now)).toList
  }

  override def update(rental: Rental): Option[Rental] = {
    if (rentals.contains(rental.id)) {
      rentals(rental.id) = rental
      Some(rental)
    } else {
      None
    }
  }

  override def delete(id: String): Boolean = {
    rentals.remove(id).isDefined
  }
}
