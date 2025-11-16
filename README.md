# Distributed Log Aggregator

A lightweight Java Spring Boot service for ingesting and querying logs from distributed microservices in (near) real time.

It exposes a small HTTP API for:
- Writing logs from any service over HTTP
- Querying logs by service name and time range
- Returning logs in a time‑sorted, consistent order even when they arrive out of order

The current implementation uses an in‑memory, thread‑safe data structure, making it ideal for demos, prototypes, and local development.

---

## Tech Stack

- Java 17
- Spring Boot 3 (web starter)
- Maven (build + dependency management)

---

## Getting Started

### Prerequisites

- JDK 17+
- Maven 3.8+ (or compatible)
- `curl` or any HTTP client (Postman, Insomnia, etc.) to exercise the API

### Clone and Build

```bash
git clone https://github.com/your-username/distributed-log-aggregator.git
cd distributed-log-aggregator
mvn clean package
```

### Run the Service

Using Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

Or run the built JAR:

```bash
java -jar target/distributed-log-aggregator-1.0.0.jar
```

By default the service starts on `http://localhost:8080`.

---

## API Overview

Base path: `http://localhost:8080`

### 1. Ingest Logs

**Endpoint**

- `POST /logs`

**Request Body**

```json
{
  "service_name": "auth-service",
  "timestamp": "2025-03-17T10:15:00Z",
  "message": "Login success"
}
```

- `service_name`: name of the service producing the log (string, required)
- `timestamp`: ISO-8601 instant (e.g. `2025-03-17T10:15:00Z`) (required)
- `message`: log message (string, required)

**Example**

```bash
curl -X POST http://localhost:8080/logs \
  -H "Content-Type: application/json" \
  -d '{
    "service_name": "auth-service",
    "timestamp": "2025-03-17T10:15:00Z",
    "message": "Login success"
  }'
```

**Responses**

- `201 Created` – log accepted and stored
- `400 Bad Request` – missing or invalid fields (e.g. bad timestamp format)

---

### 2. Query Logs

**Endpoint**

- `GET /logs`

**Query Parameters**

- `service` – service name to filter by (required)
- `start` – start of time range (inclusive, ISO-8601 instant) (required)
- `end` – end of time range (inclusive, ISO-8601 instant) (required)

**Example**

```bash
curl "http://localhost:8080/logs?service=auth-service&start=2025-03-17T10:00:00Z&end=2025-03-17T11:00:00Z"
```

**Sample Response**

```json
[
  {
    "timestamp": "2025-03-17T10:15:00Z",
    "message": "Login success"
  },
  {
    "timestamp": "2025-03-17T10:20:00Z",
    "message": "User profile loaded"
  }
]
```

Logs are always returned sorted by timestamp even if they were ingested out of order.

**Responses**

- `200 OK` – returns an array of logs (possibly empty)
- `400 Bad Request` – invalid query parameters (e.g. bad timestamp format)

---

## Internals & Design

- `LogEntry` (`src/main/java/com/example/logaggregator/model/LogEntry.java`):
  - Simple model containing `serviceName`, `timestamp` (`Instant`), and `message`.

- `InMemoryLogService` (`src/main/java/com/example/logaggregator/service/InMemoryLogService.java`):
  - Uses a `ConcurrentHashMap` keyed by `serviceName`.
  - Each service stores logs in a `ConcurrentSkipListMap<Instant, List<LogEntry>>`, providing:
    - Efficient time‑range queries via `subMap(start, end)`
    - Naturally ordered timestamps
    - Support for out‑of‑order log ingestion

- `LogController` (`src/main/java/com/example/logaggregator/controller/LogController.java`):
  - `POST /logs`: validates input, parses `Instant`, hands off to `LogService`.
  - `GET /logs`: parses query parameters, fetches from `LogService`, converts to a compact JSON representation.

---

## Limitations & Next Steps

This project is intentionally simple. Current limitations include:

- **In-memory only** – logs are lost when the application restarts.
- **Single instance** – no replication or distribution across nodes.
- **No authentication/authorization** – suitable for internal or demo environments only.

Ideas for future enhancements:

- Pluggable storage backends (e.g. JDBC, MongoDB, or a time-series database).
- Pagination and streaming for large result sets.
- Basic auth or API keys for protected deployments.
- Structured log fields (levels, correlation IDs, metadata).

---

## License

This project is provided as-is for learning and experimentation. Add your preferred license here (e.g. MIT, Apache 2.0).
