package com.example.services

import com.example.models.Book
import com.example.repositories.BookRepository

class BookService(bookRepository: BookRepository) {
  
  def createBook(name: String): Book = {
    val book = Book.create(name)
    bookRepository.create(book)
  }

  def getBook(id: String): Option[Book] = {
    bookRepository.findById(id)
  }

  def getAllBooks(): List[Book] = {
    bookRepository.findAll()
  }

  def updateBook(id: String, name: String): Option[Book] = {
    bookRepository.findById(id).flatMap { existingBook =>
      val updatedBook = existingBook.copy(name = name)
      bookRepository.update(updatedBook)
    }
  }

  def deleteBook(id: String): Boolean = {
    bookRepository.delete(id)
  }
}
