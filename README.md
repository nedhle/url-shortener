# URL Shortener Service

A high-performance URL shortening service built with:

- **Kotlin** - Modern, concise, and safe programming language
- **Spring Boot** - Robust framework for building production-ready applications
- **Docker** - Containerization for consistent development and deployment
- **Redis** - In-memory caching for high-speed URL lookups
- **Hexagonal Architecture** - Clean separation of concerns for maintainability

## Prerequisites

- Docker 20.10+
- Docker Compose 2.0+

## Quick Start

1. Clone the repository:
   ```bash
   git https://github.com/nedhle/url-shortener.git
   cd url-shortener
   ```

2. Build and start the application:
   ```bash
   docker-compose up --build -d
   ```

3. The application will be available at: http://localhost:8080

## API Endpoints

### Create Short URL
```http
POST /api
Content-Type: application/json

{
  "originalUrl": "https://example.com/very/long/url"
}
```

### Resolve Short URL
```http
GET /{shortCode}
```

## Environment Variables

You can customize the application behavior by setting these environment variables in the `docker-compose.yml` file:

- `SPRING_PROFILES_ACTIVE`: Active Spring profile (default: `dev`)
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_REDIS_HOST`: Redis host
- `SPRING_REDIS_PORT`: Redis port

## Stopping the Application

To stop all services:
```bash
docker-compose down
```

To stop and remove all containers, networks, and volumes:
```bash
docker-compose down -v
```

## Monitoring

The application includes Actuator endpoints for monitoring:
- Health: http://localhost:8080/actuator/health
- Info: http://localhost:8080/actuator/info

## Performance Considerations

### Potential Bottlenecks and Mitigations

1. **Database Load**
   - *Bottleneck*: High volume of URL resolution requests hitting the database
   - *Solution*: Multi-level caching with Redis in front of the database

2**URL Generation Collisions**
   - *Bottleneck*: Duplicate short codes during high concurrency
   - *Solution*: SnowflakeId generation

3**Rate Limiting**
   - *Bottleneck*: Potential for abuse through excessive API calls
   - *Solution*: Implement rate limiting (e.g., using Spring Cloud Gateway or Redis)

4**Scalability**
   - *Bottleneck*: Single point of failure in database
   - *Solution*: Database read replicas and connection pooling

## Monitoring and Maintenance

- **Redis Monitoring**: Track cache hit/miss ratios and memory usage
- **Database Health**: Monitor query performance and connection pools
- **Application Metrics**: Use Spring Boot Actuator for JVM and request metrics

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
