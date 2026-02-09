package com.opplan.webui.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileGatewayService {

    private final WebClient webClient;

    @Value("${opplan.service.download-endpoint}")
    private String downloadEndpoint;

    public Flux<DataBuffer> downloadFile() {
        log.info("Gateway: Forwarding download request to opplan-service");

        return webClient
                .get()
                .uri(downloadEndpoint)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .doOnComplete(() -> log.info("Gateway: File streaming completed"))
                .doOnError(error -> log.error("Gateway: Error streaming file from opplan-service", error));
    }
}
