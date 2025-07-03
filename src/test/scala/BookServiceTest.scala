package com.example

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.example.models.Book
import com.example.services.BookService
import com.example.repositories.InMemoryBookRepository

class BookServiceTest extends AnyFunSuite with Matchers {

  test("should create a book with default cost") {
    val repository = new InMemoryBookRepository()
    val service = new BookService(repository)
    
    val book = service.createBook("Test Book")
    
    book.name shouldEqual "Test Book"
    book.cost shouldEqual 10.0 // Default cost
    book.id should not be empty
  }

  test("should retrieve a book by id") {
    val repository = new InMemoryBookRepository()
    val service = new BookService(repository)
    
    val book = service.createBook("Test Book")
    val retrieved = service.getBook(book.id)
    
    retrieved shouldBe defined
    retrieved.get shouldEqual book
  }

  test("should return None for non-existent book") {
    val repository = new InMemoryBookRepository()
    val service = new BookService(repository)
    
    val retrieved = service.getBook("non-existent-id")
    
    retrieved shouldBe empty
  }

  test("should get all books") {
    val repository = new InMemoryBookRepository()
    val service = new BookService(repository)
    
    val book1 = service.createBook("Book 1")
    val book2 = service.createBook("Book 2")
    
    val allBooks = service.getAllBooks()
    
    allBooks should have size 2
    allBooks should contain(book1)
    allBooks should contain(book2)
  }

  test("should update a book") {
    val repository = new InMemoryBookRepository()
    val service = new BookService(repository)
    
    val book = service.createBook("Original Name")
    val updated = service.updateBook(book.id, "Updated Name")
    
    updated shouldBe defined
    updated.get.name shouldEqual "Updated Name"
    updated.get.id shouldEqual book.id
  }

  test("should return None when updating non-existent book") {
    val repository = new InMemoryBookRepository()
    val service = new BookService(repository)
    
    val updated = service.updateBook("non-existent-id", "New Name")
    
    updated shouldBe empty
  }

  test("should delete a book") {
    val repository = new InMemoryBookRepository()
    val service = new BookService(repository)
    
    val book = service.createBook("Test Book")
    val deleted = service.deleteBook(book.id)
    
    deleted shouldBe true
    service.getBook(book.id) shouldBe empty
  }

  test("should return false when deleting non-existent book") {
    val repository = new InMemoryBookRepository()
    val service = new BookService(repository)
    
    val deleted = service.deleteBook("non-existent-id")
    
    deleted shouldBe false
  }
}
