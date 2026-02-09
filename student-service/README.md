# Student Service

Spring Boot microservice for downloading files from AWS S3 with streaming support.

## Configuration

- **Java Version**: 17
- **Spring Boot Version**: 3.2.2
- **Build Tool**: Maven
- **Port**: 8080

## AWS Configuration

The service is configured to download from:
- **Bucket**: jmz-bucket
- **Region**: us-east-2
- **File**: test-java-sdk/Thumbnail-AWS.jpg

Ensure your AWS credentials are configured locally via `~/.aws/credentials` or environment variables.

## API Endpoints

### Download File
```
GET http://localhost:8080/api/files/download
```

This endpoint streams the file from S3 directly to the client.

## Running the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## Features

- ✅ Reactive streaming with Spring WebFlux
- ✅ Async S3 file download
- ✅ CORS configuration for Angular client
- ✅ Exception handling and logging
- ✅ Streaming response to minimize memory usage
