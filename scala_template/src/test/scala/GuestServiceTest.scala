package com.example

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.example.models.Guest
import com.example.services.GuestService
import com.example.repositories.InMemoryGuestRepository

class GuestServiceTest extends AnyFunSuite with Matchers {

  test("should create a guest") {
    val repository = new InMemoryGuestRepository()
    val service = new GuestService(repository)
    
    val guest = service.createGuest("John Doe")
    
    guest.name shouldEqual "John Doe"
    guest.id should not be empty
  }

  test("should retrieve a guest by id") {
    val repository = new InMemoryGuestRepository()
    val service = new GuestService(repository)
    
    val guest = service.createGuest("John Doe")
    val retrieved = service.getGuest(guest.id)
    
    retrieved shouldBe defined
    retrieved.get shouldEqual guest
  }

  test("should return None for non-existent guest") {
    val repository = new InMemoryGuestRepository()
    val service = new GuestService(repository)
    
    val retrieved = service.getGuest("non-existent-id")
    
    retrieved shouldBe empty
  }

  test("should get all guests") {
    val repository = new InMemoryGuestRepository()
    val service = new GuestService(repository)
    
    val guest1 = service.createGuest("John Doe")
    val guest2 = service.createGuest("Jane Smith")
    
    val allGuests = service.getAllGuests()
    
    allGuests should have size 2
    allGuests should contain(guest1)
    allGuests should contain(guest2)
  }

  test("should update a guest") {
    val repository = new InMemoryGuestRepository()
    val service = new GuestService(repository)
    
    val guest = service.createGuest("Original Name")
    val updated = service.updateGuest(guest.id, "Updated Name")
    
    updated shouldBe defined
    updated.get.name shouldEqual "Updated Name"
    updated.get.id shouldEqual guest.id
  }

  test("should return None when updating non-existent guest") {
    val repository = new InMemoryGuestRepository()
    val service = new GuestService(repository)
    
    val updated = service.updateGuest("non-existent-id", "New Name")
    
    updated shouldBe empty
  }

  test("should delete a guest") {
    val repository = new InMemoryGuestRepository()
    val service = new GuestService(repository)
    
    val guest = service.createGuest("John Doe")
    val deleted = service.deleteGuest(guest.id)
    
    deleted shouldBe true
    service.getGuest(guest.id) shouldBe empty
  }

  test("should return false when deleting non-existent guest") {
    val repository = new InMemoryGuestRepository()
    val service = new GuestService(repository)
    
    val deleted = service.deleteGuest("non-existent-id")
    
    deleted shouldBe false
  }
}
