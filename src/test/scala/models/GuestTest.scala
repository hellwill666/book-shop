package com.example.models

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GuestTest extends AnyFunSuite with Matchers {

  test("Guest should be created with name") {
    val guest = Guest.create("John Doe")
    
    guest.name shouldBe "John Doe"
    guest.id should not be empty
  }

  test("Guest should be created with custom parameters") {
    val guest = Guest(id = "test-guest-id", name = "Jane Smith")
    
    guest.id shouldBe "test-guest-id"
    guest.name shouldBe "Jane Smith"
  }
}
