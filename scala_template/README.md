# Book Rental System

A Scala-based REST API for managing book rentals built with Akka HTTP and SBT.

## Features

- **Book Management**: Create, read, update, and delete books
- **Guest Management**: Manage guests (renters) 
- **Rental System**: Rent books to guests with automatic return date calculation (1 week)
- **Return System**: Return books and track rental status
- **Query APIs**: Various endpoints to check rental states

## Project Structure

```
scala_template/
├── build.sbt                     # SBT build configuration
├── project/
│   └── build.properties         # SBT version
├── src/
│   └── main/
│       └── scala/
│           ├── Main.scala       # Application entry point
│           ├── models/          # Data models
│           │   ├── Book.scala
│           │   ├── Guest.scala
│           │   └── Rental.scala
│           ├── repositories/    # Data access layer
│           │   ├── BookRepository.scala
│           │   ├── GuestRepository.scala
│           │   └── RentalRepository.scala
│           ├── services/        # Business logic layer
│           │   ├── BookService.scala
│           │   ├── GuestService.scala
│           │   └── RentalService.scala
│           ├── controllers/     # HTTP controllers
│           │   ├── BookController.scala
│           │   ├── GuestController.scala
│           │   └── RentalController.scala
│           └── json/           # JSON serialization
│               └── JsonFormats.scala
└── README.md
```

## API Endpoints

### Books
- `GET /api/books` - Get all books
- `POST /api/books` - Create a book (body: `{"name": "Book Name"}`)
- `GET /api/books/{id}` - Get book by ID
- `PUT /api/books/{id}` - Update book (body: `{"name": "New Name"}`)
- `DELETE /api/books/{id}` - Delete book
- `GET /api/books/available` - Get available books (not currently rented)

### Guests
- `GET /api/guests` - Get all guests
- `POST /api/guests` - Create a guest (body: `{"name": "Guest Name"}`)
- `GET /api/guests/{id}` - Get guest by ID
- `PUT /api/guests/{id}` - Update guest (body: `{"name": "New Name"}`)
- `DELETE /api/guests/{id}` - Delete guest

### Rentals
- `GET /api/rentals` - Get all rentals
- `POST /api/rentals` - Rent a book (body: `{"bookId": "book-id", "guestId": "guest-id"}`)
- `GET /api/rentals/active` - Get active (not returned) rentals
- `GET /api/rentals/overdue` - Get overdue rentals
- `GET /api/rentals/guest/{guestId}` - Get rentals by guest
- `GET /api/rentals/book/{bookId}` - Get rentals by book
- `PUT /api/rentals/{id}/return` - Return a book by rental ID
- `PUT /api/rentals/book/{bookId}/return` - Return a book by book ID

### System
- `GET /` - Welcome message
- `GET /health` - Health check

## Data Models

### Book
- `id`: Unique identifier (UUID)
- `name`: Book name
- `cost`: Fixed rental cost per week (10.0)

### Guest
- `id`: Unique identifier (UUID)
- `name`: Guest name

### Rental
- `id`: Unique identifier (UUID)
- `bookId`: Reference to rented book
- `guestId`: Reference to renting guest
- `tookAt`: Rental start date/time
- `shouldReturn`: Return due date (automatically set to tookAt + 1 week)
- `returned`: Boolean indicating if book was returned

## Running the Application

### Prerequisites
- Java 8 or higher
- SBT (Scala Build Tool)

### Start the server
```bash
sbt run
```

The server will start on `http://localhost:8080`

### Example Usage

1. **Create a book:**
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"name": "Clean Code"}'
```

2. **Create a guest:**
```bash
curl -X POST http://localhost:8080/api/guests \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe"}'
```

3. **Rent a book:**
```bash
curl -X POST http://localhost:8080/api/rentals \
  -H "Content-Type: application/json" \
  -d '{"bookId": "book-id-here", "guestId": "guest-id-here"}'
```

4. **Return a book:**
```bash
curl -X PUT http://localhost:8080/api/rentals/rental-id-here/return
```

5. **Check available books:**
```bash
curl http://localhost:8080/api/books/available
```

## Technologies Used

- **Scala 3.3.3**
- **Akka HTTP 10.5.3** - REST API framework
- **Akka Actors 2.8.5** - Actor system
- **Spray JSON** - JSON serialization
- **SBT 1.10.1** - Build tool
