# Smart E-Commerce System

A full-featured e-commerce platform built with Spring Boot, providing both REST and GraphQL APIs for managing products, orders, customers, and reviews.

## Features

- User authentication and authorization
- Product catalog with categories
- Shopping cart management
- Order processing with status tracking
- Product reviews and ratings 
- Product search and filtering
- Dual API support (REST & GraphQL)
- Efficient caching with Caffeine
- Performance monitoring with AOP

## Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Database**: PostgreSQL
- **API**: REST + GraphQL
- **Cache**: Caffeine
- **Build Tool**: Maven
- **Logging**: SLF4J with Logback

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│         Controllers Layer               │
│  (REST & GraphQL Controllers)           │
├─────────────────────────────────────────┤
│         Service Layer                   │
│  (Business Logic & Validation)          │
├─────────────────────────────────────────┤
│         Store Layer                     │
│  (Transaction Management & Caching)     │
├─────────────────────────────────────────┤
│         DAO Layer                       │
│  (Database Access via JDBC)             │
├─────────────────────────────────────────┤
│         PostgreSQL Database             │
└─────────────────────────────────────────┘
```

### Key Components

- **Controllers**: Handle HTTP requests and GraphQL queries/mutations
- **Services**: Business logic, validation, and DTO mapping
- **Stores**: Transaction management, caching, and exception handling
- **DAOs**: Raw JDBC operations for database access
- **AOP Aspects**: Cross-cutting concerns (logging, performance monitoring)

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+
- Environment variables configured (see Setup)

## Setup & Installation

### 1. Environment Configuration

Create a `.env` file in the project root with:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ecommerce_db
DB_USER=your_username
DB_PASSWORD=your_password
```

### 2. Database Setup

Run the SQL scripts located in `src/main/resources/db/`:

```bash
psql -U your_username -d ecommerce_db -f src/main/resources/db.sql/db.sql.sql
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or using the JAR file
java -jar target/ecommerce-system-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## API Documentation

### REST API
- **Swagger UI**: `http://localhost:8080/api/docs`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`

### GraphQL
- **GraphQL Playground**: `http://localhost:8080/graphql`
- **Schema**: `src/main/resources/graphql/orders-schema.graphqls`

## Performance Monitoring

### How It Works

The application includes AOP-based performance monitoring that tracks:
- **Execution time**: Duration of each API call
- **Payload size**: Request/response data volume
- **Memory usage**: Heap memory consumption

Performance data is logged for both REST and GraphQL endpoints, enabling comparative analysis.

### Generate Performance Report

The system automatically logs performance metrics during runtime. To generate a comparative report:

#### Bash (Git Bash/WSL/Linux)
```bash
chmod +x generate-report.sh
./generate-report.sh
```

### Report Output

The report displays:
- **Per-endpoint metrics**: Calls, avg/min/max duration, avg payload size
- **Overall comparison**: REST vs GraphQL performance statistics
- **Performance winner**: Which API is faster and by how much

Example output:
```
==========================================================
           PERFORMANCE COMPARISON REPORT
==========================================================

REST ENDPOINTS
----------------------------------------------------------
  ProductController.getProduct(..)
    Calls:           150
    Avg Duration:    38.50ms
    Min Duration:    25ms
    Max Duration:    89ms
    Avg Payload:     245.30 bytes

GRAPHQL ENDPOINTS
----------------------------------------------------------
  ProductGraphQLController.getProduct(..)
    Calls:           45
    Avg Duration:    142.30ms
    Min Duration:    98ms
    Max Duration:    234ms
    Avg Payload:     512.80 bytes

OVERALL COMPARISON
----------------------------------------------------------
  REST is 103.80ms (73.0%) faster
  REST has 267.50 bytes (52.2%) smaller payload
==========================================================
```

## Project Structure

```
ecommerce-system/
├── src/main/java/com/example/ecommerce_system/
│   ├── aspect/          # AOP aspects for logging & performance
│   ├── config/          # Spring configuration classes
│   ├── controller/      # REST & GraphQL controllers
│   ├── dao/             # JDBC data access objects
│   ├── dto/             # Data transfer objects
│   ├── exception/       # Custom exceptions
│   ├── model/           # Domain models
│   ├── service/         # Business logic layer
│   ├── store/           # Transaction & caching layer
│   └── util/            # Utility classes
├── src/main/resources/
│   ├── db/              # Database migration scripts
│   ├── graphql/         # GraphQL schema definitions
│   └── application*.properties
├── logs/                # Application logs
└── pom.xml              # Maven configuration
```

## Testing

Run the test suite:

```bash
mvn test
```

## Caching

The application uses Caffeine cache for:
- Products
- Categories
- Customers
- Orders
- Cart items
- Reviews

Cache configuration in `application-dev.properties`:
- Max size: 1000 entries per cache
- TTL: 10 minutes

## Logging

Logs are written to:
- **Console**: All log levels
- **File**: `logs/application.log` (rotated daily)

Log levels can be configured per package in `application-dev.properties`.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

```

