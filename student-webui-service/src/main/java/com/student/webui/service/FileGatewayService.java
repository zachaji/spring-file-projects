package com.student.webui.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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

    public FileGatewayService(
            @Qualifier("streamingRestClient") RestClient streamingRestClient,
            RestClient restClient) {
        this.streamingRestClient = streamingRestClient;
        this.restClient = restClient;
    }

    public InputStream downloadFile() {
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
                    return response.getBody();
                }, false);
    }

    public byte[] downloadFileAsBytes() {
        log.info("Gateway: Forwarding byte[] download request to student-service (default connection)");

        return restClient
                .get()
                .uri(downloadBytesEndpoint)
                .retrieve()
                .body(byte[].class);
    }
}
