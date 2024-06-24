package com.chadtalty.demo.controller;

import com.chadtalty.demo.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/s3")
@Tag(name = "S3 Controller", description = "APIs for managing S3 operations")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/upload")
    @Operation(
            summary = "Upload a document to S3",
            description = "Uploads a file to the specified S3 bucket and returns the ETag.",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "File to upload",
                            required = true,
                            content =
                                    @io.swagger.v3.oas.annotations.media.Content(
                                            mediaType = "multipart/form-data",
                                            schema =
                                                    @io.swagger.v3.oas.annotations.media.Schema(
                                                            type = "object",
                                                            requiredProperties = {"file"}))))
    public ResponseEntity<String> uploadDocument(
            @RequestParam String bucketName, @RequestParam String keyName, @RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is missing");
        }
        return ResponseEntity.ok().body(s3Service.uploadDocument(bucketName, keyName, file));
    }

    @GetMapping("/download")
    @Operation(
            summary = "Retrieve a document from S3",
            description = "Retrieves a file from the specified S3 bucket and returns its content.")
    public ResponseEntity<byte[]> retrieveDocument(@RequestParam String bucketName, @RequestParam String keyName) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", keyName));

        return ResponseEntity.ok().headers(headers).body(s3Service.retrieveDocument(bucketName, keyName));
    }
}
