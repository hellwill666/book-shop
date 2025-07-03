package com.example.repositories

import com.example.models.Guest
import scala.collection.mutable

trait GuestRepository {
  def create(guest: Guest): Guest
  def findById(id: String): Option[Guest]
  def findAll(): List[Guest]
  def update(guest: Guest): Option[Guest]
  def delete(id: String): Boolean
}

class InMemoryGuestRepository extends GuestRepository {
  private val guests = mutable.Map[String, Guest]()

  override def create(guest: Guest): Guest = {
    guests(guest.id) = guest
    guest
  }

  override def findById(id: String): Option[Guest] = guests.get(id)

  override def findAll(): List[Guest] = guests.values.toList

  override def update(guest: Guest): Option[Guest] = {
    if (guests.contains(guest.id)) {
      guests(guest.id) = guest
      Some(guest)
    } else {
      None
    }
  }

  override def delete(id: String): Boolean = {
    guests.remove(id).isDefined
  }
}
