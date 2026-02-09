# Spring File Downloads - S3 Streaming Project

Full-stack application for downloading files from AWS S3 with streaming support.

## Project Structure

```
spring-file-downloads/
├── opplan-service/          # Spring Boot backend
└── opplan-client/           # Angular frontend
```

## Architecture Overview

This project demonstrates a modern full-stack architecture for streaming file downloads from AWS S3:

- **Backend**: Spring Boot 3.x with WebFlux for reactive streaming
- **Frontend**: Angular 20 with HttpClient
- **Cloud**: AWS S3 for file storage
- **Communication**: REST API with CORS support

### Data Flow

1. User clicks download button in Angular app
2. Angular sends GET request to Spring Boot API
3. Spring Boot streams file from S3 using AWS SDK
4. File is streamed back to Angular client
5. Browser saves file to downloads folder

## Prerequisites

- Java 17
- Node.js 22.x
- Maven 3.x
- AWS credentials configured (`~/.aws/credentials`)
- Access to AWS S3 bucket: jmz-bucket (us-east-2)

## Quick Start

### 1. Start the Backend

```bash
cd opplan-service
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### 2. Start the Frontend

```bash
cd opplan-client
npm install
ng serve
```

Frontend will start on `http://localhost:4200`

### 3. Test the Application

1. Open browser to `http://localhost:4200`
2. Click "Download Thumbnail-AWS.jpg" button
3. File will be downloaded from S3 to your downloads folder

## API Endpoints

### Download File
```
GET http://localhost:8080/api/files/download
```

**Response**: Binary stream (image/jpeg)

**Headers**:
- `Content-Type: application/octet-stream`
- `Content-Disposition: attachment; filename="Thumbnail-AWS.jpg"`

## Configuration

### Backend (opplan-service)

`src/main/resources/application.yml`:
```yaml
aws:
  region: us-east-2
  s3:
    bucket-name: jmz-bucket
    file-key: test-java-sdk/Thumbnail-AWS.jpg
```

### Frontend (opplan-client)

`src/app/services/file-download.ts`:
```typescript
private apiUrl = 'http://localhost:8080/api/files/download';
```

## Technologies Used

### Backend
- Spring Boot 3.2.2
- Spring WebFlux (Reactive Streaming)
- AWS SDK for Java 2.x
- Project Lombok
- Maven

### Frontend
- Angular 20.0.4
- TypeScript
- RxJS
- HttpClient
- npm

## Features

### Backend
✅ Reactive streaming with Spring WebFlux
✅ AWS S3 async client integration
✅ CORS configuration for Angular
✅ Exception handling and logging
✅ Memory-efficient streaming

### Frontend
✅ Modern Angular standalone components
✅ Reactive HTTP client
✅ Clean and responsive UI
✅ Download progress indication
✅ Success and error handling

## Security Notes

- AWS credentials should be stored securely in `~/.aws/credentials`
- Never commit AWS credentials to version control
- CORS is currently configured for `localhost:4200` - update for production
- Consider adding authentication/authorization for production use

## Troubleshooting

### Backend won't start
- Verify Java 17 is installed: `java -version`
- Check AWS credentials are configured
- Ensure port 8080 is available

### Frontend won't start
- Verify Node.js is installed: `node -v`
- Delete `node_modules` and run `npm install` again
- Ensure port 4200 is available

### Download fails
- Verify backend is running on port 8080
- Check AWS credentials have S3 read permissions
- Verify S3 bucket and file exist
- Check browser console for errors

## License

This project is for educational and development purposes.
