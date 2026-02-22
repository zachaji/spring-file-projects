package com.student.webui.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;

@Slf4j
@Service
public class FileGatewayService {

    private final RestClient streamingRestClient;
    private final RestClient restClient;

    @Value("${student.service.download-endpoint}")
    private String downloadEndpoint;

    @Value("${student.service.download-bytes-endpoint}")
    private String downloadBytesEndpoint;

    @Value("${student.service.download-resource-endpoint}")
    private String downloadResourceEndpoint;

    public FileGatewayService(
            @Qualifier("streamingRestClient") RestClient streamingRestClient,
            RestClient restClient) {
        this.streamingRestClient = streamingRestClient;
        this.restClient = restClient;
    }

    public record StreamingFile(InputStream inputStream, HttpHeaders upstreamHeaders) {}

    public StreamingFile downloadFile() {
        log.info("Gateway: Forwarding download request to student-service (pooled connection)");

        return streamingRestClient
                .get()
                .uri(downloadEndpoint)
                .exchange((request, response) -> {
                    if (response.getStatusCode().isError()) {
                        log.error("Gateway: Error response from student-service: {}", response.getStatusCode());
                        throw new RuntimeException("Failed to download file from student-service: " + response.getStatusCode());
                    }
                    log.info("Gateway: Received response from student-service, streaming to client");
                    return new StreamingFile(response.getBody(), response.getHeaders());
                }, false);
    }

    public record BytesFile(byte[] data, HttpHeaders upstreamHeaders) {}

    public BytesFile downloadFileAsBytes() {
        log.info("Gateway: Forwarding byte[] download request to student-service (default connection)");

        ResponseEntity<byte[]> response = restClient
                .get()
                .uri(downloadBytesEndpoint)
                .retrieve()
                .toEntity(byte[].class);

        log.info("Gateway: Received byte[] from student-service, size={} bytes", response.getBody().length);
        return new BytesFile(response.getBody(), response.getHeaders());
    }

    public record FileResource(Resource resource, HttpHeaders upstreamHeaders) {}

    public FileResource downloadFileAsResource() {
        log.info("Gateway: Forwarding Resource download request to student-service (streaming connection)");

        return streamingRestClient
                .get()
                .uri(downloadResourceEndpoint)
                .exchange((request, response) -> {
                    if (response.getStatusCode().isError()) {
                        log.error("Gateway: Error response from student-service: {}", response.getStatusCode());
                        throw new RuntimeException("Failed to download file from student-service: " + response.getStatusCode());
                    }

                    Resource resource = new InputStreamResource(response.getBody());

                    log.info("Gateway: Streaming as Resource");
                    return new FileResource(resource, response.getHeaders());
                }, true);
    }

    public FileResource downloadFileAsByteArrayResource() {
        log.info("Gateway: Forwarding ByteArrayResource download request to student-service (default connection)");

        ResponseEntity<byte[]> response = restClient
                .get()
                .uri(downloadBytesEndpoint)
                .retrieve()
                .toEntity(byte[].class);

        byte[] fileData = response.getBody();

        log.info("Gateway: Wrapping byte[] as ByteArrayResource, size={} bytes", fileData.length);
        return new FileResource(new ByteArrayResource(fileData), response.getHeaders());
    }

}
