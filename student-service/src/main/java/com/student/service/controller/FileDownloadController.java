package com.student.service.controller;

import com.student.service.exception.FileDownloadException;
import com.student.service.service.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> downloadFile() {
        log.info("Received request to download file");

        try {
            InputStream inputStream = s3FileService.downloadFile();
            String fileName = s3FileService.getFileName();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            log.info("Streaming file: {}", fileName);

            StreamingResponseBody responseBody = outputStream -> {
                try (inputStream) {
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

    @GetMapping(value = "/download-bytes", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFileAsBytes() {
        log.info("Received request to download file as byte[]");

        try {
            byte[] fileData = s3FileService.downloadFileAsBytes();
            String fileName = s3FileService.getFileName();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(fileData.length);

            log.info("Returning file as byte[]: {}, size={} bytes", fileName, fileData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);

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
