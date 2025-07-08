# URL Shortener Service

A Kotlin **Spring Boot 3** application that provides URL shortening and resolution capabilities following the **Hexagonal (Ports & Adapters) Architecture**.  
It exposes a REST API, stores mappings in a relational DB, and accelerates look-ups with a Redis cache.

---
## Table of Contents
1. [Architecture](#architecture)
2. [Modules](#modules)
3. [Caching](#caching)
4. [Configuration](#configuration)
5. [Running with Docker](#running-with-docker)
6. [API Reference](#api-reference)
7. [Error Handling](#error-handling)
8. [Postman Collection](#postman-collection)
9. [Testing](#testing)

---
## Architecture
The codebase embraces **Hexagonal Architecture**:

```
           ┌──────────────────────────────────────┐
           │              REST API               │
           ├──────────────────┬───────────────────┤
           │  Inbound Adapter │ Outbound Adapter  │
           │   (Controller)   │  (Persistence)    │
           └──────────┬───────┴──────────┬────────┘
                      │ ports / interfaces│
                      ▼                  ▼
                ┌─────────────── Domain ────────────────┐
                │   Use Cases  │  Models  │  Ports       │
                └──────────────┴──────────┴─────────────┘
```

* **Domain** module is technology-agnostic and contains:
  * `UrlMapping` entity
  * Use-case handlers (`CreateShortUrlUseCaseHandler`, `ResolveShortUrlUseCaseHandler`)
  * Ports (`UrlMappingPort`, `CachePort`)
* **Infrastructure** module supplies adapters that implement the ports:
  * REST controller (inbound)
  * JPA repository for Postgres
  * Redis cache adapter (outbound)

---
## Modules
| Module | Purpose |
|--------|---------|
| `domain` | Pure business logic, no Spring dependencies |
| `infrastructure` | Spring Boot app, adapters, configuration, exception handling |

Both modules are assembled by a Maven multi-module build (`pom.xml`).

---
## Caching
The `RedisCacheAdapter` implements `CachePort` and stores JSON-serialized `UrlMapping` objects in Redis:

* TTL is configurable via the property `cache.ttl-minutes` (default `2`).
* Property binding is done by `CacheProperties`, loaded from `application.yml` or environment variable `CACHE_TTL_MINUTES`.
* Flow:
  1. On **resolve**, the cache is queried first (HIT ⇒ immediate return).
  2. On **miss**, the DB is hit and the result is cached with the configured TTL.

---
## Configuration
`infrastructure/src/main/resources/application.yml` shows the defaults; important sections:

```yaml
server:
  port: 8080

cache:
  ttl-minutes: ${CACHE_TTL_MINUTES:2}
```

Database & Redis settings can be supplied through Spring profiles or environment variables:

```bash
# Example
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/url_db
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
```

Docker image uses the `docker` profile by default, but you can override:
```bash
docker run -e SPRING_PROFILES_ACTIVE=prod ... url-shortener
```

---
## Running with Docker
Build the application into a container image and run it along with PostgreSQL and Redis.

```bash
# Build image (tagged 'url-shortener')
docker build -t url-shortener .

# Example: run PostgreSQL and Redis with Docker Compose (see docker-compose.yml)
docker compose up -d postgres redis

# Run the application
#   -p 8080:8080 maps host port to container
#   --env-file .env allows overriding Spring properties

docker run --rm -p 8080:8080 --env-file .env --network host url-shortener
```

The app starts on `http://localhost:8080` (Swagger UI at `/swagger-ui.html`).

You may instead rely solely on `docker compose up` if the repository ships a compose file that defines all three services (app, db, cache).

---
## API Reference
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api` | Create a short URL. Body: `{ "originalUrl": "https://example.com" }` |
| `GET`  | `/api/{shortCode}` | Resolve a short code to its original URL |

Successful `POST` returns `201 Created` with body:
```json
{
  "id": 1,
  "originalUrl": "https://example.com",
  "shortCode": "abc123"
}
```

---
## Error Handling
Custom exceptions translate to structured error responses (`GlobalExceptionHandler`):

| Error | HTTP | Example message |
|-------|------|-----------------|
| `InvalidInputException` | 400 | "Original URL cannot be blank" |
| `UrlNotFoundException`  | 404 | "Short code not found" |
| Generic fallback        | 500 | "An unexpected error occurred" |

Error codes/messages are centralized in [`ErrorCode`](domain/src/main/kotlin/com/urlshortener/domain/exception/ErrorCode.kt).

---
## Postman Collection
A ready-made collection resides in `postman/UrlShortener.postman_collection.json` containing:
* **Create Short URL** request (stores `shortCode` env var).
* **Resolve Short URL** request using the stored variable.

Import the folder into Postman and hit **Send**.

---
## Testing
* **Unit & Integration Tests**: JUnit 5 + MockK.
* Run all tests:
  ```bash
  mvn clean test
  ```
