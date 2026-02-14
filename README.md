# Spring File Downloads - S3 Streaming Project

Full-stack application for downloading files from AWS S3 with streaming support.

## Project Structure

```
spring-file-downloads/
├── student-service/          # Spring Boot backend (upstream) - connects to AWS S3
├── student-webui-service/    # Spring Boot gateway (middle tier) - proxies requests to student-service
└── student-client/           # Angular frontend (downstream) - calls student-webui-service
```

## Architecture Overview

This project demonstrates a modern full-stack architecture for streaming file downloads from AWS S3:

- **Backend (student-service)**: Spring Boot 3.x with WebFlux — connects directly to AWS S3 via AWS SDK 2.x
- **Gateway (student-webui-service)**: Spring Boot 3.x with WebFlux — acts as a proxy/gateway between the frontend and the backend
- **Frontend (student-client)**: Angular 20 with HttpClient
- **Cloud**: AWS S3 for file storage
- **Communication**: REST API with reactive streaming and CORS support

### Data Flow

```
student-client (Angular :4200)
    ↓  HTTP GET /api/files/download
student-webui-service (Gateway :8080)
    ↓  WebClient GET /api/files/download
student-service (Backend :8088)
    ↓  AWS SDK
AWS S3 Bucket (jmz-bucket)
```

1. User clicks download button in Angular app (port 4200)
2. Angular sends GET request to the gateway service (port 8080)
3. Gateway forwards the request to the backend service (port 8088) using WebClient
4. Backend streams the file from S3 using the AWS SDK async client
5. The stream is passed back through the gateway to the Angular client
6. Browser saves file to downloads folder

## Prerequisites

- Java 17
- Node.js 22.x
- Maven 3.x
- AWS credentials configured (`~/.aws/credentials`)
- Access to AWS S3 bucket: jmz-bucket (us-east-2)

## Quick Start

### 1. Start the Backend (student-service)

```bash
cd student-service
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8088`

### 2. Start the Gateway (student-webui-service)

```bash
cd student-webui-service
mvn clean install
mvn spring-boot:run
```

Gateway will start on `http://localhost:8080`

### 3. Start the Frontend

```bash
cd student-client
npm install
ng serve
```

Frontend will start on `http://localhost:4200`

### 4. Test the Application

1. Open browser to `http://localhost:4200`
2. Click "Download Thumbnail-AWS.jpg" button
3. File will be downloaded from S3 to your downloads folder

## API Endpoints

### Gateway (student-webui-service - port 8080)

```
GET http://localhost:8080/api/files/download
```

Entry point for the Angular frontend. Proxies the request to the backend service.

**Response**: Binary stream (application/octet-stream)

**Headers**:
- `Content-Type: application/octet-stream`
- `Content-Disposition: attachment; filename="Thumbnail-AWS.jpg"`

### Backend (student-service - port 8088)

```
GET http://localhost:8088/api/files/download
```

Connects directly to AWS S3 and streams the file. Called by the gateway, not by the frontend directly.

## Configuration

### Backend (student-service)

`src/main/resources/application.yml`:
```yaml
server:
  port: 8088

aws:
  region: us-east-2
  s3:
    bucket-name: jmz-bucket
    file-key: test-java-sdk/Thumbnail-AWS.jpg
```

### Gateway (student-webui-service)

`src/main/resources/application.yml`:
```yaml
server:
  port: 8080

student:
  service:
    url: http://localhost:8088
    download-endpoint: /api/files/download
```

### Frontend (student-client)

`src/app/services/file-download.ts`:
```typescript
private apiUrl = 'http://localhost:8080/api/files/download';
```

## Technologies Used

### Backend (student-service)
- Spring Boot 3.2.2
- Spring WebFlux (Reactive Streaming)
- AWS SDK for Java 2.x
- Project Lombok
- Maven

### Gateway (student-webui-service)
- Spring Boot 3.2.2
- Spring WebFlux (Reactive Streaming)
- WebClient (non-blocking HTTP client)
- Project Lombok
- Maven

### Frontend (student-client)
- Angular 20.0.4
- TypeScript
- RxJS
- HttpClient
- npm

## Features

### Backend (student-service)
- Reactive streaming with Spring WebFlux
- AWS S3 async client integration
- Memory-efficient streaming via Flux<DataBuffer>
- Exception handling and logging

### Gateway (student-webui-service)
- Gateway pattern for service orchestration
- Reactive request forwarding with WebClient
- CORS configuration for Angular (localhost:4200)
- Streaming proxy (no buffering, passes reactive streams through)
- Extensible for cross-cutting concerns (auth, rate limiting, etc.)
- Exception handling and logging

### Frontend (student-client)
- Modern Angular standalone components
- Reactive HTTP client
- Clean and responsive UI
- Download progress indication
- Success and error handling

## Security Notes

- AWS credentials should be stored securely in `~/.aws/credentials`
- Never commit AWS credentials to version control
- CORS is currently configured for `localhost:4200` - update for production
- Consider adding authentication/authorization for production use

## Troubleshooting

### Backend (student-service) won't start
- Verify Java 17 is installed: `java -version`
- Check AWS credentials are configured
- Ensure port 8088 is available

### Gateway (student-webui-service) won't start
- Verify Java 17 is installed: `java -version`
- Ensure port 8080 is available
- Verify student-service is running on port 8088

### Frontend won't start
- Verify Node.js is installed: `node -v`
- Delete `node_modules` and run `npm install` again
- Ensure port 4200 is available

### Download fails
- Verify both backend (8088) and gateway (8080) are running
- Check AWS credentials have S3 read permissions
- Verify S3 bucket and file exist
- Check browser console for errors

## License

This project is for educational and development purposes.
