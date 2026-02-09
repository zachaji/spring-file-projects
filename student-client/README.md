# Student Client

Angular client application for downloading files from AWS S3 via the Student Service (Spring Boot backend).

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 20.0.4.

## Features

- ✅ Modern Angular standalone components
- ✅ Reactive file download from Spring Boot API
- ✅ Clean and responsive UI
- ✅ Download progress indication
- ✅ Success and error handling
- ✅ Automatic file saving to browser downloads

## Prerequisites

Ensure the Spring Boot backend (student-service) is running on `http://localhost:8080`

## Installation

```bash
npm install
```

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Usage

1. Make sure the Spring Boot backend (student-service) is running on port 8080
2. Click the "Download Thumbnail-AWS.jpg" button
3. The file will be downloaded from S3 and saved to your downloads folder

## API Integration

The client connects to the Spring Boot backend:
```
GET http://localhost:8080/api/files/download
```

**File Information:**
- Bucket: jmz-bucket
- Region: us-east-2
- File: test-java-sdk/Thumbnail-AWS.jpg

CORS is configured in the backend to allow requests from `http://localhost:4200`

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
