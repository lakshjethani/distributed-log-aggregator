# Distributed Log Aggregator

A Java Spring Boot service to ingest and query logs from distributed microservices in real time.

## Features
- `POST /logs`: Ingest logs with service name, timestamp, and message
- `GET /logs`: Query logs by service and time range
- In-memory, thread-safe storage
- Sorted logs, supports out-of-order ingestion

## Example Usage

### Ingest Log
```bash
curl -X POST http://localhost:8080/logs \
  -H "Content-Type: application/json" \
  -d '{"service_name":"auth-service","timestamp":"2025-03-17T10:15:00Z","message":"Login success"}'


git clone https://github.com/your-username/distributed-log-aggregator.git
cd distributed-log-aggregator
mvn clean install
mvn spring-boot:run
