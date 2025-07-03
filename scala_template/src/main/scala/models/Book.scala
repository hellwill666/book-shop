package com.example.models

import java.util.UUID

case class Book(
  id: String = UUID.randomUUID().toString,
  name: String,
  cost: Double = 10.0 // Fixed cost for a week for all books
)

object Book {
  def create(name: String): Book = Book(name = name)
}
