package com.opplan.service.service;

import com.opplan.service.exception.FileDownloadException;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.ByteBuffer;

@Slf4j
@Service
public class S3FileService {

    private final S3AsyncClient s3AsyncClient;
    private final String bucketName;
    private final String fileKey;
    private final DefaultDataBufferFactory bufferFactory;

    public S3FileService(
            S3AsyncClient s3AsyncClient,
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.file-key}") String fileKey) {
        this.s3AsyncClient = s3AsyncClient;
        this.bucketName = bucketName;
        this.fileKey = fileKey;
        this.bufferFactory = new DefaultDataBufferFactory();
    }

    public Flux<DataBuffer> downloadFile() {
        log.info("Starting file download from S3: bucket={}, key={}", bucketName, fileKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        return Flux.create(sink -> {
            s3AsyncClient.getObject(
                    getObjectRequest,
                    AsyncResponseTransformer.toPublisher()
            ).whenComplete((responsePublisher, throwable) -> {
                if (throwable != null) {
                    log.error("Error downloading file from S3", throwable);
                    if (throwable.getCause() instanceof S3Exception) {
                        S3Exception s3Exception = (S3Exception) throwable.getCause();
                        sink.error(new FileDownloadException(
                                "S3 Error: " + s3Exception.awsErrorDetails().errorMessage(),
                                s3Exception
                        ));
                    } else {
                        sink.error(new FileDownloadException("Failed to download file from S3", throwable));
                    }
                } else {
                    responsePublisher.subscribe(new Subscriber<ByteBuffer>() {
                        @Override
                        public void onSubscribe(Subscription subscription) {
                            subscription.request(Long.MAX_VALUE);
                        }

                        @Override
                        public void onNext(ByteBuffer byteBuffer) {
                            DataBuffer dataBuffer = bufferFactory.wrap(byteBuffer);
                            sink.next(dataBuffer);
                        }

                        @Override
                        public void onError(Throwable error) {
                            log.error("Error during file streaming from S3", error);
                            sink.error(new FileDownloadException("Error streaming file from S3", error));
                        }

                        @Override
                        public void onComplete() {
                            log.info("File download completed successfully");
                            sink.complete();
                        }
                    });
                }
            });
        });
    }

    public String getFileName() {
        return fileKey.substring(fileKey.lastIndexOf('/') + 1);
    }
}
