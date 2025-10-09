package com.miportafolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public String uploadFile(MultipartFile file)throws IOException{
        if(!file.getContentType().startsWith("video/"))  {
            throw new IllegalArgumentException("File is not a video.");
        }
        String key = "videos/" + UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;
    }
    public void deleteFile(String key){
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
                s3Client.deleteObject(deleteObjectRequest);
    }

    public String getFileUrl(String key){
        return String.format("https://%s.s3.%s.amazonaws.com/%s",bucketName,region,key);
    }
}
