package com.student.service.controller;

import com.student.service.exception.FileDownloadException;
import com.student.service.service.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileDownloadController {

    private final S3FileService s3FileService;

    @GetMapping(value = "/download")
    public ResponseEntity<StreamingResponseBody> downloadFile() {
        log.info("Received request to download file");

        try {
            S3FileService.S3File s3File = s3FileService.downloadFile();
            String fileName = s3FileService.getFileName();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
            headers.setContentType(MediaType.parseMediaType(s3File.contentType()));

            log.info("Streaming file: {}, contentType={}", fileName, s3File.contentType());

            StreamingResponseBody responseBody = outputStream -> {
                try (InputStream inputStream = s3File.inputStream()) {
                    inputStream.transferTo(outputStream);
                    outputStream.flush();
                    log.info("File download completed successfully");
                }
            };

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(responseBody);

        } catch (Exception e) {
            log.error("Error initiating file download", e);
            throw new FileDownloadException("Failed to initiate file download", e);
        }
    }

    @GetMapping(value = "/download-bytes")
    public ResponseEntity<byte[]> downloadFileAsBytes() {
        log.info("Received request to download file as byte[]");

        try {
            S3FileService.S3BytesFile s3BytesFile = s3FileService.downloadFileAsBytes();
            String fileName = s3FileService.getFileName();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
            headers.setContentType(MediaType.parseMediaType(s3BytesFile.contentType()));
            headers.setContentLength(s3BytesFile.data().length);

            log.info("Returning file as byte[]: {}, size={} bytes, contentType={}", fileName, s3BytesFile.data().length, s3BytesFile.contentType());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(s3BytesFile.data());

        } catch (Exception e) {
            log.error("Error downloading file as byte[]", e);
            throw new FileDownloadException("Failed to download file as byte[]", e);
        }
    }

    @ExceptionHandler(FileDownloadException.class)
    public ResponseEntity<String> handleFileDownloadException(FileDownloadException ex) {
        log.error("File download exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error downloading file: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred");
    }
}
