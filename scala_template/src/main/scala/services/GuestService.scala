package com.example.services

import com.example.models.Guest
import com.example.repositories.GuestRepository

class GuestService(guestRepository: GuestRepository) {
  
  def createGuest(name: String): Guest = {
    val guest = Guest.create(name)
    guestRepository.create(guest)
  }

  def getGuest(id: String): Option[Guest] = {
    guestRepository.findById(id)
  }

  def getAllGuests(): List[Guest] = {
    guestRepository.findAll()
  }

  def updateGuest(id: String, name: String): Option[Guest] = {
    guestRepository.findById(id).flatMap { existingGuest =>
      val updatedGuest = existingGuest.copy(name = name)
      guestRepository.update(updatedGuest)
    }
  }

  def deleteGuest(id: String): Boolean = {
    guestRepository.delete(id)
  }
}
