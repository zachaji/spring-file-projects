package com.student.service.service;

import com.student.service.exception.FileDownloadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class S3FileService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String fileKey;

    public S3FileService(
            S3Client s3Client,
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.file-key}") String fileKey) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.fileKey = fileKey;
    }

    public InputStream downloadFile() {
        log.info("Starting file download from S3: bucket={}, key={}", bucketName, fileKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        try {
            return s3Client.getObject(getObjectRequest);
        } catch (S3Exception e) {
            log.error("Error downloading file from S3", e);
            throw new FileDownloadException("S3 Error: " + e.awsErrorDetails().errorMessage(), e);
        } catch (Exception e) {
            log.error("Error downloading file from S3", e);
            throw new FileDownloadException("Failed to download file from S3", e);
        }
    }

    public byte[] downloadFileAsBytes() {
        log.info("Starting byte[] file download from S3: bucket={}, key={}", bucketName, fileKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        try (InputStream inputStream = s3Client.getObject(getObjectRequest)) {
            byte[] bytes = inputStream.readAllBytes();
            log.info("Byte[] file download completed, size={} bytes", bytes.length);
            return bytes;
        } catch (S3Exception e) {
            log.error("Error downloading file as bytes from S3", e);
            throw new FileDownloadException("S3 Error: " + e.awsErrorDetails().errorMessage(), e);
        } catch (IOException e) {
            log.error("Error reading file bytes from S3", e);
            throw new FileDownloadException("Failed to read file bytes from S3", e);
        }
    }

    public String getFileName() {
        return fileKey.substring(fileKey.lastIndexOf('/') + 1);
    }
}
