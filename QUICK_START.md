# Book Rental System - Quick Start Guide

## ✅ Project Status
- ✅ All tests passing
- ✅ Project compiles successfully
- ✅ Ready to run

## 🚀 Running the Application

### Start the Server
```bash
sbt run
```

The server will start on `http://localhost:8080` and display:
- Welcome message
- List of all available API endpoints
- Sample data initialization (3 books, 3 guests)

### Testing the API

#### Option 1: Use the automated test script
```bash
./test_api.sh
```

#### Option 2: Manual testing with curl

**1. Health Check**
```bash
curl http://localhost:8080/health
```

**2. Get all books**
```bash
curl http://localhost:8080/api/books
```

**3. Create a book**
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"name": "Your Book Title"}'
```

**4. Get available books**
```bash
curl http://localhost:8080/api/books/available
```

**5. Create a guest**
```bash
curl -X POST http://localhost:8080/api/guests \
  -H "Content-Type: application/json" \
  -d '{"name": "Guest Name"}'
```

**6. Rent a book**
```bash
curl -X POST http://localhost:8080/api/rentals \
  -H "Content-Type: application/json" \
  -d '{"bookId": "BOOK_ID_HERE", "guestId": "GUEST_ID_HERE"}'
```

**7. Return a book**
```bash
curl -X PUT http://localhost:8080/api/rentals/RENTAL_ID_HERE/return
```

**8. Get rental states**
```bash
# All rentals
curl http://localhost:8080/api/rentals

# Active rentals
curl http://localhost:8080/api/rentals/active

# Overdue rentals
curl http://localhost:8080/api/rentals/overdue

# Rentals by guest
curl http://localhost:8080/api/rentals/guest/GUEST_ID_HERE

# Rentals by book
curl http://localhost:8080/api/rentals/book/BOOK_ID_HERE
```

## 📋 Complete API Reference

### Books
- `GET /api/books` - Get all books
- `POST /api/books` - Create a book
- `GET /api/books/available` - Get available books (not rented)
- `GET /api/books/{id}` - Get book by ID
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book

### Guests
- `GET /api/guests` - Get all guests
- `POST /api/guests` - Create a guest
- `GET /api/guests/{id}` - Get guest by ID
- `PUT /api/guests/{id}` - Update guest
- `DELETE /api/guests/{id}` - Delete guest

### Rentals
- `GET /api/rentals` - Get all rentals
- `POST /api/rentals` - Rent a book
- `GET /api/rentals/active` - Get active rentals
- `GET /api/rentals/overdue` - Get overdue rentals
- `GET /api/rentals/guest/{id}` - Get rentals by guest
- `GET /api/rentals/book/{id}` - Get rentals by book
- `PUT /api/rentals/{id}/return` - Return a book
- `PUT /api/rentals/book/{id}/return` - Return book by book ID

### System
- `GET /` - Welcome message
- `GET /health` - Health check

## 🎯 Key Features Implemented

✅ **Book Management** - CRUD operations for books
✅ **Guest Management** - CRUD operations for guests/renters
✅ **Rental System** - Rent books with automatic return date (1 week)
✅ **Return System** - Return books and update status
✅ **Query APIs** - Multiple endpoints to check different rental states
✅ **Fixed Cost** - All books cost $10 per week
✅ **Sample Data** - Pre-populated data for testing

## 🧪 Running Tests

```bash
sbt test
```

All tests should pass, covering:
- Model creation and validation
- Service layer business logic
- Repository operations
- Basic integration scenarios

## 📁 Project Structure

```
src/main/scala/
├── Main.scala              # Application entry point
├── models/                 # Data models (Book, Guest, Rental)
├── repositories/           # Data access layer (in-memory)
├── services/              # Business logic layer
├── controllers/           # HTTP controllers/routes
└── json/                  # JSON serialization

src/test/scala/            # Test files
```

The project is now ready for use! 🎉
