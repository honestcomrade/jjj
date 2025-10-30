# Spring Boot JDBC Demo

A Spring Boot application demonstrating JDBC functionality with PostgreSQL, Flyway migrations, and Testcontainers integration tests.

## Features
- Spring Boot 3.2.0 with Java 21
- JDBC with PostgreSQL
- Flyway database migrations
- RESTful API endpoints for message management
- Integration tests with Testcontainers (real PostgreSQL)

## Prerequisites
- Java 21
- PostgreSQL (for local development)
- Docker (for running tests with Testcontainers)

## Database Schema

The application uses Flyway migrations located in `src/main/resources/db/migration/`:
- `V1__create_messages_table.sql` - Creates the messages table with id and message columns

## API Endpoints

### Health Check
```
GET /health
```
Returns: `OK`

### Get All Messages
```
GET /messages
```
Returns: Array of message objects with `id` and `message` fields

### Create Message
```
POST /messages
Content-Type: application/json

{
  "message": "Your message text"
}
```
Returns: Created message object with generated `id`

### Create Table (Manual Testing)
```
GET /createTable
```
Creates the messages table and inserts initial data (use only if not using Flyway)

## Running the Application

### Using Gradle
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Building
```bash
./gradlew build
```

## Configuration

Configure your PostgreSQL connection in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
```

## Running Tests

The project includes integration tests using Testcontainers that spin up a real PostgreSQL database in Docker.

### Prerequisites for Tests
- Docker must be running on your machine

### Run Tests
```bash
./gradlew test
```

Tests will:
1. Start a PostgreSQL container via Testcontainers
2. Run Flyway migrations automatically
3. Execute integration tests against the real database
4. Clean up containers after tests complete

## Project Structure
```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── DemoApplication.java      # Main application class
│   │   ├── DemoController.java       # REST controller
│   │   └── Message.java              # Message entity
│   └── resources/
│       ├── application.properties    # App configuration
│       └── db/migration/             # Flyway migrations
│           └── V1__create_messages_table.sql
└── test/
    └── java/com/example/demo/
        └── DemoControllerIntegrationTest.java  # Integration tests
```

## Technology Stack
- **Spring Boot 3.2.0** - Application framework
- **Spring JDBC** - Database access
- **PostgreSQL** - Production database
- **Flyway** - Database migration tool
- **Testcontainers** - Integration testing with real databases
- **JUnit 5** - Testing framework
- **Gradle** - Build tool