package com.example.repositories

import com.example.models.Book
import scala.collection.mutable

trait BookRepository {
  def create(book: Book): Book
  def findById(id: String): Option[Book]
  def findAll(): List[Book]
  def update(book: Book): Option[Book]
  def delete(id: String): Boolean
}

class InMemoryBookRepository extends BookRepository {
  private val books = mutable.Map[String, Book]()

  override def create(book: Book): Book = {
    books(book.id) = book
    book
  }

  override def findById(id: String): Option[Book] = books.get(id)

  override def findAll(): List[Book] = books.values.toList

  override def update(book: Book): Option[Book] = {
    if (books.contains(book.id)) {
      books(book.id) = book
      Some(book)
    } else {
      None
    }
  }

  override def delete(id: String): Boolean = {
    books.remove(id).isDefined
  }
}
