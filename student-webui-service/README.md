# Student WebUI Service

Spring Boot gateway service that acts as an intermediary between the Angular frontend and the student-service backend.

## Architecture

```
Angular (4200) → student-webui-service (8080) → student-service (8088) → AWS S3
```

## Configuration

- **Java Version**: 17
- **Spring Boot Version**: 3.2.2
- **Build Tool**: Maven
- **Port**: 8080

## Backend Service Configuration

The gateway is configured to forward requests to:
- **Backend URL**: http://localhost:8088
- **Download Endpoint**: /api/files/download

## API Endpoints

### Download File (Gateway)
```
GET http://localhost:8080/api/files/download
```

This endpoint:
1. Receives request from Angular client
2. Forwards request to student-service on port 8088
3. Streams response back to Angular client

## Running the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## Features

- ✅ Reactive streaming with Spring WebFlux
- ✅ WebClient for calling backend service
- ✅ CORS configuration for Angular client
- ✅ Exception handling and logging
- ✅ Gateway pattern for future extensibility
- ✅ Streaming response (no buffering)

## Purpose

This service acts as a gateway/proxy layer that:
- Provides a single entry point for the frontend
- Can be extended to include authentication, rate limiting, etc.
- Allows for service orchestration and aggregation
- Enables future migration to API Gateway patterns
