package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.example.controllers.{BookController, GuestController, RentalController}
import com.example.services.{BookService, GuestService, RentalService}
import com.example.repositories.{InMemoryBookRepository, InMemoryGuestRepository, InMemoryRentalRepository}
import com.example.json.JsonFormats
import JsonFormats._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import java.sql.DriverManager

object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "book-rental-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    // Load configuration
    val config = ConfigFactory.load()
    val dbHost = config.getString("database.host")
    val dbPort = config.getInt("database.port")
    val dbName = config.getString("database.name")
    val dbUser = config.getString("database.user")
    val dbPassword = config.getString("database.password")
    val dbUrl = s"jdbc:postgresql://$dbHost:$dbPort/$dbName"

    println(s"Connecting to database: $dbUrl")
    println(s"Database user: $dbUser")

    // Test database connection
    try {
      Class.forName("org.postgresql.Driver")
      val connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
      println("Database connection successful!")
      connection.close()
    } catch {
      case e: Exception =>
        println(s"Database connection failed: ${e.getMessage}")
        println("Continuing with in-memory repositories...")
    }

    // Initialize repositories
    val bookRepository = new InMemoryBookRepository()
    val guestRepository = new InMemoryGuestRepository()
    val rentalRepository = new InMemoryRentalRepository()

    // Initialize services
    val bookService = new BookService(bookRepository)
    val guestService = new GuestService(guestRepository)
    val rentalService = new RentalService(rentalRepository, bookRepository, guestRepository)

    // Initialize controllers
    val bookController = new BookController(bookService, rentalService)
    val guestController = new GuestController(guestService)
    val rentalController = new RentalController(rentalService)

    // Create some sample data
    initializeSampleData(bookService, guestService)

    // Define routes
    val routes: Route = pathPrefix("api") {
      concat(
        bookController.routes,
        guestController.routes,
        rentalController.routes
      )
    } ~
    path("") {
      get {
        complete("Book Rental System API is running!")
      }
    } ~
    path("health") {
      get {
        complete("OK")
      }
    }

    val bindingFuture = Http()(system.classicSystem).newServerAt("0.0.0.0", 8080).bind(routes)

    println(s"Server online at http://0.0.0.0:8080/")
    println("Available endpoints:")
    println("  GET  /                     - Welcome message")
    println("  GET  /health               - Health check")
    println("  GET  /api/books            - Get all books")
    println("  POST /api/books            - Create a book")
    println("  GET  /api/books/available  - Get available books")
    println("  GET  /api/books/{id}       - Get book by ID")
    println("  PUT  /api/books/{id}       - Update book")
    println("  DELETE /api/books/{id}     - Delete book")
    println("  GET  /api/guests           - Get all guests")
    println("  POST /api/guests           - Create a guest")
    println("  GET  /api/guests/{id}      - Get guest by ID")
    println("  PUT  /api/guests/{id}      - Update guest")
    println("  DELETE /api/guests/{id}    - Delete guest")
    println("  GET  /api/rentals          - Get all rentals")
    println("  POST /api/rentals          - Rent a book")
    println("  GET  /api/rentals/active   - Get active rentals")
    println("  GET  /api/rentals/overdue  - Get overdue rentals")
    println("  GET  /api/rentals/guest/{id} - Get rentals by guest")
    println("  GET  /api/rentals/book/{id}  - Get rentals by book")
    println("  PUT  /api/rentals/{id}/return - Return a book")
    println("  PUT  /api/rentals/book/{id}/return - Return book by book ID")
    
    // Don't wait for input in Docker environment
    val isDocker = System.getenv("ENVIRONMENT") == "production"
    if (!isDocker) {
      println("Press RETURN to stop...")
      StdIn.readLine()
      
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    } else {
      println("Running in production mode...")
      // Keep the application running
      scala.util.control.Breaks.breakable {
        while (true) {
          Thread.sleep(1000)
        }
      }
    }
  }

  private def initializeSampleData(bookService: BookService, guestService: GuestService): Unit = {
    // Create sample books
    val book1 = bookService.createBook("The Scala Programming Language")
    val book2 = bookService.createBook("Functional Programming in Scala")
    val book3 = bookService.createBook("Programming in Scala")
    
    // Create sample guests
    val guest1 = guestService.createGuest("Alice Johnson")
    val guest2 = guestService.createGuest("Bob Smith")
    val guest3 = guestService.createGuest("Charlie Brown")

    println("Sample data initialized:")
    println(s"  Books: ${book1.name} (${book1.id}), ${book2.name} (${book2.id}), ${book3.name} (${book3.id})")
    println(s"  Guests: ${guest1.name} (${guest1.id}), ${guest2.name} (${guest2.id}), ${guest3.name} (${guest3.id})")
  }
}
