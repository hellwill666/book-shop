package com.example.json

import com.example.models.{Book, Guest, Rental}
import spray.json._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JsonFormats extends DefaultJsonProtocol {
  
  // Custom formatter for LocalDateTime
  implicit object LocalDateTimeFormat extends JsonFormat[LocalDateTime] {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    def write(dateTime: LocalDateTime): JsValue = JsString(dateTime.format(formatter))
    
    def read(value: JsValue): LocalDateTime = value match {
      case JsString(dateTimeString) => LocalDateTime.parse(dateTimeString, formatter)
      case _ => throw DeserializationException("Expected ISO LocalDateTime string")
    }
  }

  // Model formatters
  implicit val bookFormat: RootJsonFormat[Book] = jsonFormat3(Book.apply)
  implicit val guestFormat: RootJsonFormat[Guest] = jsonFormat2(Guest.apply)
  implicit val rentalFormat: RootJsonFormat[Rental] = jsonFormat6(Rental.apply)

  // List formatters - these are needed for List[T] serialization
  implicit val bookListFormat: RootJsonFormat[List[Book]] = listFormat[Book]
  implicit val guestListFormat: RootJsonFormat[List[Guest]] = listFormat[Guest]
  implicit val rentalListFormat: RootJsonFormat[List[Rental]] = listFormat[Rental]

  // Request/Response case classes and their formatters
  case class CreateBookRequest(name: String)
  implicit val createBookRequestFormat: RootJsonFormat[CreateBookRequest] = jsonFormat1(CreateBookRequest.apply)

  case class CreateGuestRequest(name: String)
  implicit val createGuestRequestFormat: RootJsonFormat[CreateGuestRequest] = jsonFormat1(CreateGuestRequest.apply)

  case class RentBookRequest(bookId: String, guestId: String)
  implicit val rentBookRequestFormat: RootJsonFormat[RentBookRequest] = jsonFormat2(RentBookRequest.apply)

  case class ErrorResponse(error: String)
  implicit val errorResponseFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse.apply)

  case class SuccessResponse(message: String)
  implicit val successResponseFormat: RootJsonFormat[SuccessResponse] = jsonFormat1(SuccessResponse.apply)
}
