package com.example.controllers

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import com.example.services.GuestService
import com.example.json.JsonFormats
import JsonFormats._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

class GuestController(guestService: GuestService) {

  val routes: Route = pathPrefix("guests") {
    concat(
      // GET /guests - Get all guests
      pathEnd {
        get {
          val guests = guestService.getAllGuests()
          complete(StatusCodes.OK, guests)
        }
      },
      // POST /guests - Create a guest
      pathEnd {
        post {
          entity(as[CreateGuestRequest]) { request =>
            val guest = guestService.createGuest(request.name)
            complete(StatusCodes.Created, guest)
          }
        }
      },
      // GET /guests/{id} - Get guest by ID
      path(Segment) { id =>
        get {
          guestService.getGuest(id) match {
            case Some(guest) => complete(guest)
            case None => complete(StatusCodes.NotFound, ErrorResponse(s"Guest with id $id not found"))
          }
        }
      },
      // PUT /guests/{id} - Update guest
      path(Segment) { id =>
        put {
          entity(as[CreateGuestRequest]) { request =>
            guestService.updateGuest(id, request.name) match {
              case Some(guest) => complete(guest)
              case None => complete(StatusCodes.NotFound, ErrorResponse(s"Guest with id $id not found"))
            }
          }
        }
      },
      // DELETE /guests/{id} - Delete guest
      path(Segment) { id =>
        delete {
          if (guestService.deleteGuest(id)) {
            complete(StatusCodes.NoContent)
          } else {
            complete(StatusCodes.NotFound, ErrorResponse(s"Guest with id $id not found"))
          }
        }
      }
    )
  }
}
