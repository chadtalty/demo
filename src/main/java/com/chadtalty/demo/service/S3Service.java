package com.chadtalty.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@Slf4j
public class S3Service {

    @Autowired
    private S3Client s3Client;

    /**
     * Uploads a file to S3 and returns the ETag.
     *
     * @param bucketName the name of the S3 bucket
     * @param keyName the key name for the uploaded document
     * @param file the file to upload
     * @return the ETag of the uploaded document
     */
    public String uploadDocument(String bucketName, String keyName, MultipartFile file) {
        try {
            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder().bucket(bucketName).key(keyName).build();

            RequestBody requestBody = RequestBody.fromBytes(file.getBytes());

            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, requestBody);

            log.info("Document uploaded with ETag: {}", putObjectResponse.eTag());
            return putObjectResponse.eTag();
        } catch (Exception e) {
            log.error("Failed to upload document: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload document", e);
        }
    }

    /**
     * Retrieves a file from S3 and returns its content as a byte array.
     *
     * @param bucketName the name of the S3 bucket
     * @param keyName the key name of the document to retrieve
     * @return byte array of the retrieved document
     */
    public byte[] retrieveDocument(String bucketName, String keyName) {
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder().bucket(bucketName).key(keyName).build();

        return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
    }
}
