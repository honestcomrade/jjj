# Concurrency Race Condition Reproduction

A Spring Boot application designed to reproduce and demonstrate concurrent database race conditions when multiple threads attempt to create entities with unique constraints under READ COMMITTED isolation level.

## Purpose

This project demonstrates the classic **get-or-create race condition** that occurs when multiple threads concurrently try to insert entities with unique constraints. It showcases:

1. **The Problem**: Without proper retry or isolation logic, only one thread succeeds while others fail with duplicate key violations

## Features
- Spring Boot 3.2.0 with Java 21
- JdbcTemplate (no JPA/Hibernate) for direct SQL control
- PostgreSQL 15 with Docker Compose
- Flyway database migrations
- Entity graph: Parent → Child1/Child2 → Grandchild1 → BusinessModel
- Unique constraints on child entities to enforce race conditions
- Integration tests with Testcontainers demonstrating concurrent creation
- Request sequence tracking to trace execution order

## Architecture

### Entity Graph
```
Parent (seeded "default-parent")
  ├─→ Child1 (UNIQUE per parent_id, name)
  ├─→ Child2 (UNIQUE per parent_id, name)
  └─→ Child1 → Grandchild1 (UNIQUE per child1_id, name)
         └─→ BusinessModel (references all above entities)
```

### Race Condition Scenario

When 100 concurrent requests attempt to create a BusinessModel with identical child entity names:

**Without Retry Logic** (Before Fix):
- Batch 1: 1 thread creates entities → SUCCESS, 9 threads hit duplicate key → FAIL
- Batch 2-10: Same pattern, ~10 total successes


## Prerequisites
- Java 21
- Docker & Docker Compose
- Gradle 8.4+

## Quick Start

### 1. Start PostgreSQL Database
```bash
docker compose up -d
```

This starts PostgreSQL 15 on port 5432 with database `jjj_db`.

### 2. Run the Application
```bash
./gradlew bootRun
```

Application runs on http://localhost:8081

### 3. Check Health
```bash
curl http://localhost:8081/actuator/health
```

## Database Schema

Migrations in `src/main/resources/db/migration/`:
- `V1__initial_schema.sql` - Creates parent/child/grandchild/business_model tables with unique constraints
- `V2__seed_default_parent.sql` - Inserts default parent entity

## API Endpoints

### Health Check
```bash
GET /actuator/health
```
Returns database connection status and application health.

### Create Business Model
```bash
POST /business-models
Content-Type: application/json

{
  "name": "my-business-model",
  "type": "model-type",
  "parentId": 1,
  "child1Name": "child1-a",
  "child2Name": "child2-a",
  "grandchild1Name": "grandchild1-a",
  "requestSequence": 1
}
```

Returns the created BusinessModel with all entity IDs populated.

## Testing the Race Condition

### Run Integration Tests
```bash
./gradlew test --tests BusinessModelControllerIntegrationTest
```

The `concurrentRequests_triggerUniqueConstraintViolations` test:
1. Launches 100 concurrent requests in batches of 10
2. All requests use **identical child entity names** to force contention
3. Tracks which request sequences succeed/fail
4. Validates that exactly 1 of each child entity is created
5. Shows request-by-request logging with entity IDs

### Expected Test Output

**Without Retry Logic (Current Implementation - Demonstrates the Problem):**
```
[Request #0] FIRING with: child1Name=child1-concurrent-shared, child2Name=child2-concurrent-shared, ...
[Request #0] ✓ SUCCESS → BusinessModel[id=1, child1Id=1, child2Id=1, grandchild1Id=1]
[Request #1] FIRING with: child1Name=child1-concurrent-shared, ...
[Request #1] ✗ FAILED - DuplicateKeyException: unique constraint "uq_child1_parent_name"
[Request #2] ✗ FAILED - DuplicateKeyException: unique constraint "uq_child1_parent_name"
...
Successes: ~10 (1 per batch of 10 concurrent threads)
Failures: ~90
Child1 entities: 1
Child2 entities: 1  
Grandchild1 entities: 1
```

Only 1 thread per batch succeeds; the other 9 fail with duplicate key violations because they don't retry after the race condition.

### View Test Results
After running tests, open the HTML report:
```bash
open build/reports/tests/test/index.html
```

The report includes detailed stack traces showing duplicate key violations and retry logic in action.

## Implementation Details

### Service Layer Pattern (BusinessModelService)

**Current Implementation (WITHOUT retry logic - demonstrates the race condition):**

```java
private Child1 getOrCreateChild1(Long parentId, String name) {
    try {
        // Try to find existing entity first
        return child1Service.getByParentAndName(parentId, name);
    } catch (DataNotFoundException ex) {
        // Not found, try to create it
        Child1 child1 = new Child1();
        child1.setParentId(parentId);
        child1.setName(name);
        return child1Service.save(child1);  // ❌ No catch for DuplicateKeyException
    }
}
```

**Problem with current approach:**
- Thread 1: find() → not found → insert() → SUCCESS ✓
- Thread 2: find() → not found → insert() → DuplicateKeyException ✗ (propagates up, request fails)
- Result: 90% of concurrent requests fail

**The Fix (to be implemented):**

```java
private Child1 getOrCreateChild1(Long parentId, String name) {
    try {
        return child1Service.getByParentAndName(parentId, name);
    } catch (DataNotFoundException ex) {
        try {
            Child1 child1 = new Child1();
            child1.setParentId(parentId);
            child1.setName(name);
            return child1Service.save(child1);
        } catch (DuplicateKeyException ex2) {
            // ✓ Catch race condition and retry find
            return child1Service.getByParentAndName(parentId, name);
        }
    }
}
```

## Configuration

Application settings in `src/main/resources/application.properties`:

```properties
# Database connection (Docker Compose PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
spring.datasource.username=yourusername
spring.datasource.password=yourpassword

# Server configuration
server.address=0.0.0.0
server.port=8081

# Actuator health endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

# Flyway automatic migrations
spring.flyway.baseline-on-migrate=true
```

## Project Structure

```
src/main/java/com/messager/application/
├── Controllers/
│   └── BusinessModelController.java      # REST API endpoint
├── Services/
│   ├── BusinessModelService.java         # Orchestrates entity creation with retry logic
│   ├── Child1Service.java
│   ├── Child2Service.java
│   ├── Grandchild1Service.java
│   └── ParentService.java
├── Dao/
│   ├── BusinessModelDao.java             # JDBC data access
│   ├── Child1Dao.java
│   ├── Child2Dao.java
│   ├── Grandchild1Dao.java
│   └── ParentDao.java
└── Models/
    ├── BusinessModel.java                 # Domain entities
    ├── Child1.java
    ├── Child2.java
    ├── Grandchild1.java
    ├── ParentEntity.java
    └── dto/
        └── BusinessModelCreateRequest.java

src/test/java/com/messager/tests/
└── BusinessModelControllerIntegrationTest.java  # Concurrency test with 100 threads
```

## Key Learnings

This project demonstrates:

1. **READ COMMITTED Isolation**: Default PostgreSQL isolation level allows race conditions
1. **Time-of-Check-to-Time-of-Use (TOCTOU)**: Gap between checking entity existence and inserting it creates race window
1. **The Problem**: Without retry logic, 90% of concurrent requests fail with duplicate key violations
1. **Request Tracking**: Using sequence numbers to trace concurrent execution order and see which threads win/lose races

## Troubleshooting

### Database Connection Issues
```bash
# Check if PostgreSQL is running
docker compose ps

# View database logs
docker compose logs db

# Restart database
docker compose restart db
```

### Test Failures
- Ensure Docker is running before running tests
- Testcontainers automatically manages database lifecycle
- Check test report in `build/reports/tests/test/index.html`

## License

MIT

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