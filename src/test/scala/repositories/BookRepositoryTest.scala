package com.example.repositories

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.example.models.Book

class BookRepositoryTest extends AnyFunSuite with Matchers {

  test("InMemoryBookRepository should create and retrieve books") {
    val repository = new InMemoryBookRepository()
    val book = Book.create("Test Book")
    
    val createdBook = repository.create(book)
    createdBook shouldBe book
    
    val foundBook = repository.findById(book.id)
    foundBook shouldBe Some(book)
  }

  test("InMemoryBookRepository should return None for non-existent book") {
    val repository = new InMemoryBookRepository()
    
    val foundBook = repository.findById("non-existent-id")
    foundBook shouldBe None
  }

  test("InMemoryBookRepository should list all books") {
    val repository = new InMemoryBookRepository()
    val book1 = Book.create("Book 1")
    val book2 = Book.create("Book 2")
    
    repository.create(book1)
    repository.create(book2)
    
    val allBooks = repository.findAll()
    allBooks should contain theSameElementsAs List(book1, book2)
  }

  test("InMemoryBookRepository should update existing book") {
    val repository = new InMemoryBookRepository()
    val book = Book.create("Original Title")
    repository.create(book)
    
    val updatedBook = book.copy(name = "Updated Title")
    val result = repository.update(updatedBook)
    
    result shouldBe Some(updatedBook)
    repository.findById(book.id) shouldBe Some(updatedBook)
  }

  test("InMemoryBookRepository should not update non-existent book") {
    val repository = new InMemoryBookRepository()
    val book = Book.create("Test Book")
    
    val result = repository.update(book)
    result shouldBe None
  }

  test("InMemoryBookRepository should delete existing book") {
    val repository = new InMemoryBookRepository()
    val book = Book.create("Test Book")
    repository.create(book)
    
    val deleted = repository.delete(book.id)
    deleted shouldBe true
    repository.findById(book.id) shouldBe None
  }

  test("InMemoryBookRepository should not delete non-existent book") {
    val repository = new InMemoryBookRepository()
    
    val deleted = repository.delete("non-existent-id")
    deleted shouldBe false
  }
}
