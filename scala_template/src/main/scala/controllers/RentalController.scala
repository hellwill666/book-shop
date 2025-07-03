package com.example.controllers

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import com.example.services.RentalService
import com.example.json.JsonFormats
import JsonFormats._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

class RentalController(rentalService: RentalService) {

  val routes: Route = pathPrefix("rentals") {
    concat(
      // GET /rentals - Get all rentals
      pathEnd {
        get {
          val rentals = rentalService.getAllRentals()
          complete(StatusCodes.OK, rentals)
        }
      },
      // POST /rentals - Rent a book
      pathEnd {
        post {
          entity(as[RentBookRequest]) { request =>
            rentalService.rentBook(request.bookId, request.guestId) match {
              case Right(rental) => complete(StatusCodes.Created, rental)
              case Left(error) => complete(StatusCodes.BadRequest, ErrorResponse(error))
            }
          }
        }
      },
      // GET /rentals/active - Get active rentals
      path("active") {
        get {
          val activeRentals = rentalService.getActiveRentals()
          complete(StatusCodes.OK, activeRentals)
        }
      },
      // GET /rentals/overdue - Get overdue rentals
      path("overdue") {
        get {
          val overdueRentals = rentalService.getOverdueRentals()
          complete(StatusCodes.OK, overdueRentals)
        }
      },
      // GET /rentals/guest/{guestId} - Get rentals by guest
      path("guest" / Segment) { guestId =>
        get {
          val rentals = rentalService.getRentalsByGuest(guestId)
          complete(StatusCodes.OK, rentals)
        }
      },
      // GET /rentals/book/{bookId} - Get rentals by book
      path("book" / Segment) { bookId =>
        get {
          val rentals = rentalService.getRentalsByBook(bookId)
          complete(StatusCodes.OK, rentals)
        }
      },
      // PUT /rentals/{id}/return - Return a book
      path(Segment / "return") { id =>
        put {
          rentalService.returnBook(id) match {
            case Right(rental) => complete(rental)
            case Left(error) => complete(StatusCodes.BadRequest, ErrorResponse(error))
          }
        }
      },
      // PUT /rentals/book/{bookId}/return - Return a book by book ID
      path("book" / Segment / "return") { bookId =>
        put {
          rentalService.returnBookByBookId(bookId) match {
            case Right(rental) => complete(rental)
            case Left(error) => complete(StatusCodes.BadRequest, ErrorResponse(error))
          }
        }
      },
      // GET /rentals/{id} - Get rental by ID
      path(Segment) { id =>
        get {
          rentalService.getRental(id) match {
            case Some(rental) => complete(rental)
            case None => complete(StatusCodes.NotFound, ErrorResponse(s"Rental with id $id not found"))
          }
        }
      }
    )
  }
}
