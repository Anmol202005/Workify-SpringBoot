package com.workify.auth.service;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.workify.auth.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    // Injecting S3 bucket name from application.properties or application.yml
    @Value("${aws_bucket_name}")
    private String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;

    }

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        String fileName = folderName + "/" + file.getOriginalFilename()+currentUser.getId();

        // Upload file to S3
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.Private)); // Ensure it's private

        return fileName; // Return the file key
    }

    // Method to generate a pre-signed URL for downloading a file
    public URL generateDownloadUrl(String fileKey) {
        // Set expiration for the URL (10 minutes in this case)
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10; // 10 minutes
        expiration.setTime(expTimeMillis);

        // Generate the URL for the file
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    }
    public URL generateViewUrl(String fileKey) {
        // Set the expiration time for the pre-signed URL (e.g., 1 hour)
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;  // 1 hour
        expiration.setTime(expTimeMillis);

        // Create ResponseHeaderOverrides to set Content-Disposition to inline
        ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();
        responseHeaders.setContentDisposition("inline; filename=\"" + fileKey + "\"");

        // Create GeneratePresignedUrlRequest and set the method, expiration, and response headers
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration)
                        .withResponseHeaders(responseHeaders);

        // Generate and return the pre-signed URL
        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    }
    public URL generateViewPresignedUrl( String objectKey) {
        // Set expiration time for the presigned URL
        Date expiration = new Date();
        long expTimeMillis = System.currentTimeMillis() + 3600 * 1000; // 1 hour expiration
        expiration.setTime(expTimeMillis);

        // Create the presigned URL request
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey)
                .withMethod(HttpMethod.GET) // HTTP GET method
                .withExpiration(expiration); // Set the expiration time

        // Add the response-content-disposition parameter to display the file inline
        request.addRequestParameter("response-content-disposition", "inline; filename=\"" + objectKey + "\"");

        // Optionally, set the Content-Type (e.g., for PNG images)
        request.addRequestParameter("response-content-type", "image/png");

        // Generate the presigned URL
        URL presignedUrl = amazonS3.generatePresignedUrl(request);
        return presignedUrl;
    }
}
