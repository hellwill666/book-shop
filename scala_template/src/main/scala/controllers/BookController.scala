package com.example.controllers

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import com.example.services.{BookService, RentalService}
import com.example.json.JsonFormats
import JsonFormats._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

class BookController(bookService: BookService, rentalService: RentalService) {

  val routes: Route = pathPrefix("books") {
    concat(
      // GET /books/available - Get available books (must come before other routes)
      path("available") {
        get {
          val availableBooks = rentalService.getAvailableBooks()
          complete(StatusCodes.OK, availableBooks)
        }
      },
      // GET /books - Get all books
      pathEnd {
        get {
          val books = bookService.getAllBooks()
          complete(StatusCodes.OK, books)
        }
      },
      // POST /books - Create a book
      pathEnd {
        post {
          entity(as[CreateBookRequest]) { request =>
            val book = bookService.createBook(request.name)
            complete(StatusCodes.Created, book)
          }
        }
      },
      // GET /books/{id} - Get book by ID
      path(Segment) { id =>
        get {
          bookService.getBook(id) match {
            case Some(book) => complete(book)
            case None => complete(StatusCodes.NotFound, ErrorResponse(s"Book with id $id not found"))
          }
        }
      },
      // PUT /books/{id} - Update book
      path(Segment) { id =>
        put {
          entity(as[CreateBookRequest]) { request =>
            bookService.updateBook(id, request.name) match {
              case Some(book) => complete(book)
              case None => complete(StatusCodes.NotFound, ErrorResponse(s"Book with id $id not found"))
            }
          }
        }
      },
      // DELETE /books/{id} - Delete book
      path(Segment) { id =>
        delete {
          if (bookService.deleteBook(id)) {
            complete(StatusCodes.NoContent)
          } else {
            complete(StatusCodes.NotFound, ErrorResponse(s"Book with id $id not found"))
          }
        }
      }
    )
  }
}
