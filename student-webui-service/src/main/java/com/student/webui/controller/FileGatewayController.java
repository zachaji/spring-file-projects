package com.student.webui.controller;

import com.student.webui.service.FileGatewayService;
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
public class FileGatewayController {

    private final FileGatewayService fileGatewayService;

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> downloadFile() {
        log.info("Gateway: Received download request from client");

        try {
            InputStream inputStream = fileGatewayService.downloadFile();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "Thumbnail-AWS.jpg");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            log.info("Gateway: Streaming file to client");

            StreamingResponseBody responseBody = outputStream -> {
                try (inputStream) {
                    inputStream.transferTo(outputStream);
                    outputStream.flush();
                    log.info("Gateway: File streaming completed");
                }
            };

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(responseBody);

        } catch (Exception e) {
            log.error("Gateway: Error initiating file download", e);
            throw new RuntimeException("Failed to download file", e);
        }
    }

    @GetMapping(value = "/download-bytes", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFileAsBytes() {
        log.info("Gateway: Received byte[] download request from client");

        try {
            byte[] fileData = fileGatewayService.downloadFileAsBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "Thumbnail-AWS.jpg");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(fileData.length);

            log.info("Gateway: Returning file as byte[], size={} bytes", fileData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);

        } catch (Exception e) {
            log.error("Gateway: Error downloading file as byte[]", e);
            throw new RuntimeException("Failed to download file as byte[]", e);
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error("Gateway: Runtime exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error downloading file: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("Gateway: Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred");
    }
}
